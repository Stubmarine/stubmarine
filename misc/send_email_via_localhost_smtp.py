import smtplib
from email.mime.text import MIMEText

to = 'Adam <adam@example.com>'
sender = 'Sender <sender@example.com>'
subject = 'Test Subject'
body = 'Hello World!'

msg = MIMEText(body)
msg['Subject'] = subject
msg['From'] = sender
msg['To'] = to

s = smtplib.SMTP('localhost:8081')
s.sendmail(sender, [to], msg.as_string())
