
#include<iostream>
#include<string>
#include<cstring>
using namespace std;

void Compiler_c(string _path, string _file);
void data_delete_c(string _path);

int main(int argc, char ** argv){
    if(argc == 3){
        system("cls");
        string file = argv[1];
        string extension = file.substr(file.find("."));
        if(extension == ".c"){
            Compiler_c(file.substr(0, file.find(".")), argv[2]);
        }else if(extension == ".a51"){

        }else cout << "此檔案不是 .c or .a51!" << endl;
    }else cout<<"輸入格式有誤!";

    return 1;
}

void Compiler_c(string _path, string _file){
    string
    SDCC_compiler_file = ".\\Tool\\sdcc\\bin\\sdcc.exe ",
    SDCC_packihx       = ".\\Tool\\sdcc\\bin\\\\packihx.exe ",
    get_lib            = "-I D:\\Code\\school\\Sophomore\\8051\\lib",
    Copy_lib_file      = _file+"\\lib",
    get_data_file      = _path + ".c ",
    put_data_file      = _path + ".ihx ",
    hex_data_file      = _path+".hex ";
    if(!system((SDCC_compiler_file + get_data_file + "-o "+put_data_file + get_lib).c_str())){
        cout << ("========================= Compiler c pass! =========================") << endl;
        system(( SDCC_packihx + put_data_file+ " > "+ hex_data_file).c_str());
        data_delete_c(_path);
    }else cout << ("================================ Compiler c error! ================================") << endl;
}

void data_delete_c(string _path){
    remove((_path+".asm").c_str());
    remove((_path+".ihx").c_str());
    remove((_path+".lk").c_str());
    remove((_path+".lst").c_str());
    remove((_path+".map").c_str());
    remove((_path+".mem").c_str());
    remove((_path+".rel").c_str());
    remove((_path+".rst").c_str());
    remove((_path+".sym").c_str());
}