while(1):
    hz = int(input())
    hz = int(1 / int(hz) / 2 * 1000000)
    print("T =",hz)
    print("TH =", hex((65536 - hz) // 256))
    print("TL =", hex((65536 - hz) % 256))