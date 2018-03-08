cmake_minimum_required(VERSION 3.9)
project(r8brain_free_src_master)

set(CMAKE_CXX_STANDARD 11)

add_executable(r8brain_free_src_master
        DLL/r8bsrc.cpp
        DLL/r8bsrc.h
        DLL/r8bsrc.pas
        other/calcCorrTable.cpp
        other/calcErrorTable.cpp
        other/icon.png
        other/License.txt
        other/r8bdoxy.txt
        CDSPBlockConvolver.h
        CDSPFIRFilter.h
        CDSPFracInterpolator.h
        CDSPProcessor.h
        CDSPRealFFT.h
        CDSPResampler.h
        CDSPSincFilterGen.h
        custom.cpp
        example.cpp
        fft4g.h
        r8bbase.cpp
        r8bbase.h
        r8bconf.h
        r8butil.h
        README.md)
