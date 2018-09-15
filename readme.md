# 系統架構與環境
  * 安裝電腦作為 Mysql server 
  * 以 eclipse 開啟 java 專案並 import mysql-connector-java.jar 檔
  * 在 java 程式中連至 localhost，並以 root 登入
  
# 介面說明 
  * 上方 menu 能選擇使用功能。 
  * 左方是查詢需要的輸入，不同的查詢會有不同的介面。
  * 左下方按鈕送出查詢。
  * 右方會以 table 方式成查詢結果。
  
# entity
  * tudent 學生資料   
    * student_id 學生編號   
    * student_name 學生姓名   
    * sex 學生性別   
    * grade 學生年級   
  * class 課資料 
    * class_id 課編號 
    * class_name 課名稱 
    * teacher 指導老師 
  * classroom 教室 
    * classroom_id 教室編號 
    * department_id 管理系所 
    * maxnum 最多容納人數 
  * department 系所 * 
    * department_id 系所編號 
    * department_name 系所名稱 
    * director 系所領導人 
  * dorm 宿舍 
    * dorm_id 宿舍編號 
    * sex 住宿性別需求 
    * maxnum 最多住宿人數 
    
# relation
  * major 主修 
    * student_id 學生 
    * department_name 主修系所
  * live 住宿 
    * student_id 學生 
    * dorm_id 住宿宿舍
  * takeclass 修課 
    * num 編號 
    * student_id 修課學生 
    * class_id 課程 
  * assistant 擔任助教 
    * num 編號 
    * student_id 擔任助教的學生 
    * class_id 課程 
  * participate 參與開一堂課 
    * class_id 課程 
    * classroom_id 使用教室 
    * department_id 開課系所 
    * class_time 上課時間 
  * distance 宿舍與教室距離 
    * num 編號 
    * dorm_id 宿舍 
    * classroom_id 教室 
    * distance 宿舍與教室距離 
  
# 執行方式
   * `java -jar dbms.jar`
