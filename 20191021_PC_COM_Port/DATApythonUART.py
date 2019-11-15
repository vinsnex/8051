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
    
#按下欲傳送資料設定按鈕
def clickbt(x):
    if(databutton[x]["bg"]=="#ffffff"):#如果點選按鈕視窗為白色
        databutton[x]["bg"]="#00ff00"#將此按鈕設為綠色
        outdatalabel["text"]=outdatalabel["text"]+databutton[x]["text"]#將準備傳送值加按鈕上的數值
    else:#否則
        databutton[x]["bg"]="#ffffff"#將此視窗設為白色
        outdatalabel["text"]=outdatalabel["text"]-databutton[x]["text"]#將準備傳送值減按鈕上的數值
        
#按下傳送按鈕所執行的動作
def outdata():
    #將outdatalabel["text"]裡的數值當作ASCII直接轉換成字串並輸出UART
    ser.write(struct.pack('B', outdatalabel["text"]))
    
#讀取接收到的數值
def readdata():
    global ser
    #當連線狀態開啟 就一直準備接收數值 因為是多線程的關係 所以並不會造成主程式當機
    while (linklabel["bg"]=="#00ff00"):#當中斷連線時即跳出迴圈
        try:
            if(ser.in_waiting>0):#如果有數值接收進來 ser.in_waiting 代表數值數量 當read(n)過後 會自動減少n個
                x=ser.read(1)#讀取一個字至x
                inputdata=ord(x)#取得x(字元)的ASCII碼到inputdata裡
                y=inputdata#將inputdata複製至y裡 顯示過程中會變動到y值
                for i in range(7,-1,-1):#i=7~0 變化量為-1
                    if(y%2==1): #如果判斷中的位元為1
                        datalabel[i]['bg']="#ff0000"#將此BIT代表的LABEL設為紅色
                    else:#否則
                        datalabel[i]['bg']="#ffffff"#將此BIT代表的LABEL設為白色
                    y=y>>1#將y以二位元模式右移一位元(整除二也可以達到一樣效果 y=int(y/2))
        except Exception as e:
            pass

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
databutton=[]#創建按鈕陣列(此時陣列是全空
datalabel=[]#創建LABEL陣列(此時陣列是全空
for i in range(0,8):# i=0~7 變化量為+1
    #創建欲傳送資料設定按鈕 並放入databutton陣列裡
    databutton.append(Button(dataf,width=5,text=pow(2,7-i),bg="#ffffff",command=lambda x=i:clickbt(x)))
    databutton[i].grid(row=0,column=i)#擺放按鈕
    
    #創建接收資料顯示LABEL 並放入datalabel陣列裡
    datalabel.append(Label(dataf, width=5,bg="#ffffff",borderwidth=2, relief="groove"))
    datalabel[i].grid(row=2,column=i,sticky=N+E+S+W)#擺放LABEL
    
outdatalabel=Label(dataf,text=0)#創建郁傳送數值顯示LABEL
outdatalabel.grid(row=1,column=0,columnspan=4,sticky=N+E+S+W)#擺放LABEL

outdatabutton=Button(dataf,text="傳送",state="disabled",command=lambda:outdata())#創建[傳送]按鈕
outdatabutton.grid(row=1,column=4,columnspan=4,sticky=N+E+S+W)#擺放[傳送]按鈕

loop()#啟用loop
#將關閉視窗這個動作連結到on_closing()函式
root.protocol("WM_DELETE_WINDOW", on_closing)
root.mainloop()#視窗動作一直執行 直到被關閉


