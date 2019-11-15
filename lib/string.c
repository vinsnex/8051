
char strcmp(char *str1, char *str2);

char strcmp(char *str1, char *str2){
    while(*str1 == *str2  ){
        if(*str1 == 0) return 1;
        str1++;
        str2++;
    };
    return 0;
}

