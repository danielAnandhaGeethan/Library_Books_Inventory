import pandas as pd
import mysql.connector

db_host = "localhost"  
db_port = 3309
db_name = "library"  
db_user = "root"
db_password = "root"

# Connect to MySQL database
try:
  connection = mysql.connector.connect(host=db_host, port=db_port, database=db_name, user=db_user, password=db_password)
except mysql.connector.Error as err:
  print("Error connecting to database:", err)
  exit()


# Read data from Books.xlsx
try:
  df = pd.read_excel("D:\CODE\Library_Books_Inventory\Books.xlsx")
except FileNotFoundError:
  print("Error: Books.xlsx file not found!")
  connection.close()
  exit()


# Prepare SQL statement (replace with your actual column names)
sql = """INSERT INTO books (bookTitle, bookAuthor, bookISBN) VALUES ("{0}","{1}", "{2}")"""


# Insert data into books table row by row
for index, row in df.iterrows():
  try:
    cursor = connection.cursor()
    cursor.execute(sql.format(row.iloc[0], row.iloc[1], row.iloc[2]))
    connection.commit()
  except mysql.connector.Error as err:
    print(f"Error inserting data for row {index+1}: {err}")
    connection.rollback()  # Rollback the transaction on error


# Close connection
connection.close()
print("Data insertion completed (if no errors encountered).")
