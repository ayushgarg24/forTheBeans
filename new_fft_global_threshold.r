library(tuneR)
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
  #threshold_equation <- eval((3.64*(f/1000)^(-0.8))-6.5*exp(-0.6((f/1000)-3.3)^2)+((10^-3)*(f/1000)^4))
  val <- 4
  threshold_equation <- eval((f^2))
  #Plot Threshold Equation
  plot(threshold_equation)
  return(threshold_equation)
}

#Remove Rows of Two Dimensional Dataframe Under Curve
remove_quiet <- function(fft_freq) {
  
  #Indexer for the Threshold
  index_list <- 1
 
  #Loop through each row of the Transform FFT
  for (i in 1:length(fft_freq)){
    #Call Threshold in Quiet Function
    if (fft_freq[index_list] < get_threshold_in_quiet(index_list)){
      print (index_list)
      fft_freq <- fft_freq[-index_list]
      #If to convert to 0 instead of deleting
      #fft_freq[index_list] <- 0
    }
    else{
      index_list <- index_list+1
    }
  }
  return(fft_freq)
}

#Store Final Dataframe with Global Threshold Calculated
final_dataframe <- remove_quiet(x_decNorm)