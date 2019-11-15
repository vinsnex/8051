import sys
import os
import shutil
#////////////////////////////////////////////////////////////////////////////////////#
#---------------------------------------- .c ----------------------------------------#
def data_delete_c(_path, _file):
    os.remove(_path+".asm")
    os.remove(_path+".ihx")
    os.remove(_path+".lk")
    os.remove(_path+".lst")
    os.remove(_path+".map")
    os.remove(_path+".mem")
    os.remove(_path+".rel")
    os.remove(_path+".rst")
    os.remove(_path+".sym")

def Compiler_c(_path, _file):
    SDCC_compiler_file = ".\\Tool\\sdcc\\bin\\sdcc.exe "
    SDCC_packihx       = ".\\Tool\\sdcc\\bin\\\\packihx.exe ";
    get_lib            = "-I D:\\Code\\school\\Sophomore\\8051\\lib";
    Copy_lib_file      = _file+"\\lib"
    get_data_file      = _path + ".c ";
    put_data_file      = _path + ".ihx ";
    hex_data_file      = _path+".hex ";

    if(os.path.isfile(hex_data_file)):
        os.remove(hex_data_file);
    # if(not os.path.isdir(Copy_lib_file)): 
    #     shutil.copytree(".\\lib", Copy_lib_file) #Copy lib

    if(not os.system( SDCC_compiler_file + get_data_file + "-o" + put_data_file + get_lib)):
        os.system( SDCC_packihx + put_data_file+ " > "+ hex_data_file);
        os.system("cls");
        print("========================= Compiler c pass! =========================")
        data_delete_c(_path, _file);
    else: print("================================ Compiler c error! ================================")
    
#\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\#

#////////////////////////////////////////////////////////////////////////////////////#
#---------------------------------------- A51 ---------------------------------------#
def data_delete_a51(_path):
    os.remove(_path + ".lst")

def Compiler_A51(_path):
    if(not os.system(".\\Tool\\asem5113\\asemw.exe " + _path + ".a51" )):
        print("================================ Assembler a51 pass! ================================")
        data_delete_a51(_path);
    else: print("================================ Assembler a51 error! ================================")
#\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\#

if __name__ == "__main__":
    if(len(sys.argv) == 3):
        os.system("cls");
        file = os.path.splitext(sys.argv[1])
        if(file[-1].lower() == ".c"):
            Compiler_c(file[0], sys.argv[2])
        elif(file[-1].lower() == ".a51"):
            Compiler_A51(file[0])
        else:print("此檔案不是 .c or .a51")
    else:print("輸入格式有誤")