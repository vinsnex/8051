from serial import *   #UART
from  serial.tools import list_ports #UART
from tkinter import *   #GUI
from tkinter import ttk #GUI
import struct   #用於數轉換成字串 因為serial的write只能代入字串
import threading  # 用於多線程函式 可邊設定輸出時邊等待輸入數值

baudrate="9600"#鮑率
parity='N'#矯驗方式  N-無校驗  O-奇校驗  E-偶校驗
bytesize=8 #數據大小
stopbits=1 #停止位元

runloop=0
#每隔一秒執行一次的地方
def loop():
    global t,runloop,comlist,ser
    runloop=1#標記此loop正在執行
    find_all_serial()#列出電腦中所有通信埠(COM)
    try:
        p=comlist.index(ser.port)#查看連接中裝置是否有被拔除 或是ser是否為存在
    except Exception as e:#如果無此裝置將會進入錯誤
        try:
            if(ser.isOpen()):#端口狀態為打開?
                ser.close() #關閉端口
        except Exception as e:
            pass
        killlinkbutton["state"] = "disabled"#將[中斷連線]按鈕停用
        linklabel["bg"]="#ff0000"#設定狀態顯示背景顏色為紅色
        linkbutton["state"] = "active"#使[開始連線]按鈕可以被點選
        outdatabutton["state"] ="disabled"#將[傳送]按鈕停用
    t = threading.Timer(1, loop)#設定t為在1秒後呼叫loop程式  可達成每一秒執行一次
    t.start()#啟用t
    runloop=0#標記此loop執行結束

#列出電腦中所有通信埠(COM)
def find_all_serial():
    global comlist
    strcomlist=[]#通信埠名稱列表
    comlist=[]#通信埠代號列表
    try:
        for com in list_ports.comports():
            strcomlist.append(str(com))#接通信埠完整名稱放入陣列
            comlist.append(str(com).split(' ')[0])#取得通信埠代號至陣列(COMx)
        numberChosen['values']=strcomlist#將名稱列表放置選單中以供選擇
    except Exception as e:
        pass
    
#開啟通信埠
def link():
    global parity,bytesize,stopbits,ser #同步全域變數
    try:
        del ser     #嘗試將ser清空 如果失敗就算了 代表裡面沒有東西
    except Exception as e:
        pass
    try:
        ser=Serial(number.get()[:4],baudrate,timeout=0, writeTimeout=0)#開啟通信埠
        ser.parity=parity#設定矯驗模式
        ser.bytesize=bytesize#設定資料大小
        ser.stopbits=stopbits#設定停止位元
        #創建讀取數值的多線程函式thread_read
        thread_read = threading.Thread(target=readdata)
        thread_read.setDaemon(True)#將thread_read線程設為守護線程 
        thread_read.start()#啟用線程thread_read
        linklabel["bg"]="#00ff00"#將顯示連線狀態LABEL的背景設為綠色
        linkbutton["state"] = "disabled"#停用[開始連線]按鈕
        killlinkbutton["state"] = "active"#啟用[中斷連線]按鈕
        outdatabutton["state"] ="active"#啟用資料輸出按鈕功能
    except Exception as e:
        linklabel["bg"]="#ff0000"#將顯示連線狀態LABEL的背景設為紅色
        
#關閉通信埠
def killlink():
    global ser
    try:
        if(ser.isOpen()):#端口狀態為打開?
            ser.close() #關閉端口
    except Exception as e:
        pass
    killlinkbutton["state"] = "disabled"#停用[中斷連線]按鈕
    linkbutton["state"] = "active"#啟用[開始連線]按鈕
    outdatabutton["state"] ="disabled"#停用[傳送]按鈕
    linklabel["bg"]="#ff0000"#將顯示連線狀態LABEL的背景設為紅色

#按下傳送按鈕所執行的動作
def outdata():
    s=outstr.get()#取得以輸入字串
    if(nlbutton['text']=='\\n'):#判斷是否需要加入NL
        s=s+'\n'#字串加入NL
    if(crbutton['text']=='\\r'):#判斷是否需要加入CR
        s=s+'\r'#字串加入CR
    ser.write(s.encode())#將數值傳送出去
    outstr.set('')#清除輸入框
#將文字輸入框裡按下ENTER時也會直接把數值傳送出去
def keydown(key):
    if(key.keysym == "Return"):#如果按下的鍵為Enter
        if(outdatabutton["state"]!="disabled"):#傳送按鈕是否啟用
            outdata()#執行傳送按鈕的功能
#按下清除按鈕所執行的動作    
def cls():
    strinput.delete(0.0, END)#將strinput內容重頭到尾清除

#讀取接收到的數值
def readdata():
    global ser,strinput
    while (linklabel["bg"]=="#00ff00"):#如果連線狀態啟用
        try:
            if(ser.in_waiting>0):#如果有輸入字串
                s=ser.read(ser.in_waiting)#將字串全部讀入s裡
                str(s, encoding = "utf-8")#改變s字串的編碼
                strinput.insert(END, s)#在strinput結尾加上s字串
        except Exception as e:
            pass
        
#設定傳送字串是否傳送NL
def newline():
    if(nlbutton['text']=='\\n'):#如果按鈕中字串為\n
        nlbutton['text']=' '#將按鈕中字串設為一個空格
    else:#否則
        nlbutton['text']='\\n'#將按鈕中字串設為\n
        
#設定傳送字串時是否傳送CR
def retorno_de_carro():
    if(crbutton['text']=='\\r'):#如果按鈕中字串為\r
        crbutton['text']=' '#將按鈕中字串設為一個空格
    else:#否則
        crbutton['text']='\\r'#將按鈕中字串設為\r
        
#視窗關閉時執行的程式
def on_closing():
    killlink()#關閉UART連線(呼叫[中斷連線]按鈕功能)
    #增加多線程thread_read為close() 以免在判斷loop是否跑完時導致當機
    thread_read = threading.Thread(target=close)
    thread_read.start()#啟用線程thread_read
#關閉前動作
def close():
    global runloop,t,reading,ser
    while(runloop):#等待LOOP程式跑完
        pass
    t.cancel()#停止跑下一次的loop
    try:
        if(ser.isOpen()):#端口狀態為打開?
            ser.close() #關閉端口
    except Exception as e:
        pass
    root.destroy()#關閉視窗

#---------------------主程式(GUI外觀配置)---------------------
root=Tk()#創建視窗
root.title("pyUART")#設定視窗標題

settingf=Frame(root)#創建框架settingf
settingf.pack(padx =10,pady =10)#擺放框架settingf

number = StringVar()#設定number為浮動字串
numberChosen = ttk.Combobox(settingf, width=30, textvariable=number)#創建下拉式清單
numberChosen.grid(column=0, row=0,columnspan=2)#擺放下拉式清單

linkbutton=Button(settingf,text="開啟連線",command=lambda:link())#創建[開啟連線]按鈕
linkbutton.grid(column=0,row=1,sticky=N+E+S+W)#擺放[開啟連線]按鈕

killlinkbutton=Button(settingf,text="中斷連線",state="disabled",command=lambda:killlink())#創建[中斷連線]按鈕
killlinkbutton.grid(column=1,row=1,sticky=N+E+S+W)#擺放[中斷連線]按鈕

linklabel=Label(settingf,bg="#ff0000")#創建LABEL 用來顯示連線狀態
linklabel.grid(column=0,row=2,columnspan=2,sticky=N+E+S+W)#擺放LABEL


dataf=Frame(root)#創建框架dataf
dataf.pack(padx =10,pady =10)#擺放框架dataf

outstr=StringVar()#設定outstr為一個浮動字串
outdataentry=Entry(dataf,textvariable = outstr)#創建輸入框
outdataentry.grid(row=0,column=0, columnspan=8,sticky=N+E+S+W)#擺放輸入框
outdataentry.bind("<Key>", keydown)#將輸入框中的鍵盤事件連接到函式keydown

nlbutton=Button(dataf,text='\\n',width=2,command=lambda:newline())#創建按鈕
nlbutton.grid(row=0,column=8, sticky=N+E+S+W)#擺放按鈕

crbutton=Button(dataf,text='\\r',width=2,command=lambda:retorno_de_carro())#創建按鈕
crbutton.grid(row=0,column=9, sticky=N+E+S+W)#擺放按鈕

outdatabutton=Button(dataf,text="傳送",state="disabled",command=lambda:outdata())#創建[傳送]按鈕
outdatabutton.grid(row=0,column=10,sticky=N+E+S+W)#擺放[傳送]按鈕

clsbutton=Button(dataf,text="清除",command=lambda:cls())#創建[清除]按鈕
clsbutton.grid(row=0,column=11,sticky=N+E+S+W)#擺放[清除]按鈕

getstrf=Frame(dataf)#創建框架getstrf置dataf裡
getstrf.grid(row=2,column=0, columnspan=12,sticky=N+E+S+W)#擺放框架getstrf

showScrollbar = Scrollbar(getstrf)#創建卷軸
showScrollbar.pack(side = RIGHT,fill = Y)#擺放卷軸

strinput = Text(getstrf,yscrollcommand = showScrollbar.set)#創建顯示框
strinput.pack(side = RIGHT,fill = Y)#擺放顯示框

showScrollbar.config( command = strinput.yview )#將卷軸事件與顯示框互相連接

loop()#啟用loop
#將關閉視窗這個動作連結到on_closing()函式
root.protocol("WM_DELETE_WINDOW", on_closing)
root.mainloop()#視窗動作一直執行 直到被關閉

