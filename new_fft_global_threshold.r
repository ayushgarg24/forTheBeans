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
x_orig <- sapply(x, function(x) as.integer(x))
x_orig <- sapply(x_orig, function(x) x/abs(x))
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
  #plot(threshold_equation)
  return(threshold_equation)
}

index <- 0
thresholding <- function(val, original_matrix) {
  index <<- index + 1
  if (val < get_threshold_in_quiet(index)){
    #print (index)
    #fft_freq <- fft_freq[-i]
    #If to convert to 0 instead of deleting
    #x <- 0
    return(0)
  }
  return(x[index])
}

final_dataframe <- mapply(thresholding, x_decNorm, x)

doInverse <- function(output_dataframe) {
  inv_fft <- ifft(output_dataframe)
  inv_data_ts <- ts(inv_fft, frequency = 44100)
  plot(inv_data_ts)
  int_inv_data_ts <- as.integer(inv_data_ts)
  plot(int_inv_data_ts)
  play(int_inv_data_ts)
  
  return(int_inv_data_ts)
  
}

final_byte_data <- doInverse(final_dataframe)