library(tuneR)
library(signal)
library(audio)
# Read in File
file <- readWave(file.choose())

#Convert to a Numeric Data Series for Input to FFT
data_ts <- ts(file@left, frequency = 44100)
the_type <- typeof(data_ts)
numeric_data_ts <- as.numeric(data_ts)

#Compute FFT
x <- fft(numeric_data_ts)
x_abs <- Mod(x)
x_norm <- x_abs/(length(x_abs)/2)

#Transform FFT to freq by decibels domain
x_decNorm <- sapply(x_norm, function(x) 10*log10(x))
plot(x_decNorm, type = "l")

#Calculate Threshold in Quiet Curve
get_threshold_in_quiet <- function(f){
  threshold_equation <- eval((3.64*(f/1000)^(-0.8)) -6.5*exp(-0.6*((f/1000)-3.3)^2)+((10^-3)*(f/1000)^4))
  #threshold_equation <- eval((f^2))
  #Plot Threshold Equation
  plot(threshold_equation)
  return(threshold_equation)
}

#Remove Rows of Two Dimensional Dataframe Under Curve
remove_quiet <- function(fft_freq) {
  
  #Loop through each row of the Transform FFT
  #for (i in 1:length(fft_freq)){
  for (i in 1:500){
    #Call Threshold in Quiet Function
    if (fft_freq[i] < get_threshold_in_quiet(i)){
      print (i)
      #fft_freq <- fft_freq[-i]
      #If to convert to 0 instead of deleting
      fft_freq[i] <- 0
    }
  }
  return(fft_freq)
}

#Store Final Dataframe with Global Threshold Calculated
final_dataframe <- remove_quiet(x_decNorm)

doInverse <- function(output_dataframe) {
  x_inv_decNorm <- sapply(output_dataframe, function(x) 10^(x/10))
  x_inv_norm <- x_inv_decNorm*(length(x_abs)/2)
  #Do Inverse FFT, Convert Back to Integer Time Series
  inv_fft <- ifft(x_inv_norm)
  inv_data_ts <- ts(inv_fft, frequency = 44100)
  plot(inv_data_ts)
  int_inv_data_ts <- as.integer(inv_data_ts)
  plot(int_inv_data_ts)
  play(int_inv_data_ts)
  
  return(int_inv_data_ts)
  
}

final_byte_data <- doInverse(final_dataframe)