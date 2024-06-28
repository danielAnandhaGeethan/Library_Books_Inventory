import pandas as pd
import mysql.connector
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

db_host = os.getenv("db_host") 
db_port = os.getenv("db_port") 
db_name = os.getenv("db_name") 
db_user = os.getenv("db_user") 
db_password = os.getenv("db_password") 

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
