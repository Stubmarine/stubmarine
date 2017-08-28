import sendgrid
import sys
import os
from sendgrid.helpers.mail import *

argv = sys.argv
if len(argv) >= 2 and argv[1] == "production":
    host = "https://wallraff.cfapps.io"
else:
    host = "http://localhost:8080"

if "SENDGRID_TOKEN" not in os.environ:
    print("!!! Error: SENDGRID_TOKEN env var not set!")
    exit(1)

token = os.environ["SENDGRID_TOKEN"]
if token is None or token == '':
    print("!!! Error: SENDGRID_TOKEN should be a non-empty valid token!")
    exit(1)

print("Sending SendGrid email to " + host)

sg = sendgrid.SendGridAPIClient(apikey=token, host=host+"/eapi/sendgrid")
from_email = Email("test@example.com")
to_email = Email("adam@example.com")
subject = "Sending with SendGrid is Fun"
content = Content("text/plain", "and easy to do anywhere, even with Python")
mail = Mail(from_email, subject, to_email, content)

personalization = Personalization()
personalization.add_to(Email("noizwaves@example.com"))
mail.add_personalization(personalization)

response = sg.client.mail.send.post(request_body=mail.get())
print(response.status_code)
print(response.body)
print(response.headers)

