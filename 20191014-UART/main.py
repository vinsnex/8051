import os
data = {"GET.A51", "PUCT.A51"}
for i in data:
    os.system("asemw " + str(i))
    print()