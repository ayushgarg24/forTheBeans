library(tuneR)
library(wavelets)
library(audio)
#songOne <- readWave("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\pipe.wav")
songOne <- readWave("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\first20.wav")
#out <- readWave("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\out.wav")
out <- readWave("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\out.wav")

#vals <- read.csv("myfile.csv")

songLeft <- songOne@left
songRight <- songOne@right
outLeft <- out@left
outRight <- out@right

#save(songLeft, file = "save.txt", ascii = TRUE)
#dput(songLeft, file = "dput.txt")

#input <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\in.txt")
#javaRev <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\javaRev.txt")
#shorts <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\shorts.txt")
coefs <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\files\\rObjects\\coefs.txt")
comp <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\files\\rObjects\\huff.txt")
#javaRev <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\javaRev.txt")
#shorts <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\shorts.txt")
#coefs <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\coefs.txt")
#mid <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\mid.txt")
#output <- dget("F:\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\out.txt")
#input <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\in.txt")
#mid <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\mid.txt")
#output <- dget("C:\\users\\alexr\\OneDrive\\2017-18\\Advanced Computer Science\\Projects\\JWave\\src\\out.txt")

timeArray <- (0:(27784-1))# / 44100
timeArray <- timeArray #* 1000 #scale to milliseconds

timeSeries <- ts(songLeft, frequency = 44100)
dataFrame <- as.data.frame(songLeft)
vectoR <- as.vector(songLeft)
dwtProduct <- dwt(vectoR)
dwtInverse <- idwt(dwtProduct)

trans <- as.data.frame(songLeft)

timeS <- ts(songLeft, start = 0, end = 0.63, frequency = 44100)
df <- data.frame(timeS, timeArray)

s <- ts(df, frequency = 44100)

sprod <- dwt(timeS)

#aligned <- align(sprod)

withtime <- dwt(s)

res <- idwt(sprod)
resA <- idwt(aligned)

rev <- dwt.backward(as.numeric(sprod@W[[6]]), as.numeric(sprod@V[[6]]), sprod@filter)

revtime <- dwt.backward(as.numeric(withtime@W[[7]][,1]), as.numeric(withtime@V[[7]][,1]), withtime@filter)
revTimes <- dwt.backward(as.numeric(withtime@W[[11]][,2]), as.numeric(withtime@V[[11]][,2]), withtime@filter)

timeRev <- ts(rev, frequency = 44100)