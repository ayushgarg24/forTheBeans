library(tuneR)
library(signal)
library(audio)
library(utils)
library(Rcpp)

sourceCpp("C:\\Users\\alexr\\Documents\\r8brain-free-src-master\\r8brain-free-src-master\\custom.cpp")


songOne <- readWave("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\files\\inputs\\first20.wav")
mp3 <- readMP3("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\files\\inputs\\mp.mp3")

down <- downsample(songOne, 44100/10)

songLeft <- songOne@left

bf <- butter(3, 0.1)

filtered <- filter(bf, songLeft)

FT <- fft(songLeft)

IFT <- ifft(FT)

i <- as.integer(IFT)

resampled <- resample(songLeft, 1, 2, 5)
resampled <- as.integer(resampled)

decimated <- decimate(songLeft, 10, ftype = "fir")
d <- as.integer(decimated)

z <- fftfilt(rep(1, 10)/10, songLeft)
zI <- as.integer(z)
