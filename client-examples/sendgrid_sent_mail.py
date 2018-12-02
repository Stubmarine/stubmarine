import sys

import sendgrid
from sendgrid.helpers.mail import *

import python_http_client

argv = sys.argv
if len(argv) not in [3]:
    print('Usage: python send_email_via_sendgrid.py (stub|real) <token>')
    exit(1)

if argv[1] == "stub":
    host = "http://localhost:8080"
    sandbox = False
elif argv[1] == "real":
    host = "https://api.sendgrid.com"
    sandbox = True
else:
    print('Usage: python send_email_via_sendgrid.py (stub|real) <token>')
    exit(1)

token = argv[2]

print("Sending SendGrid email to " + host)

sg = sendgrid.SendGridAPIClient(apikey=token)
from_email = Email("test@example.com")
to_email = Email("adam@example.com", "Adam")
subject = "Sending with SendGrid is Fun"
content = Content("text/plain", "and easy to do anywhere, even with Python")
mail = Mail(from_email, subject, to_email, content)

personalization = Personalization()
personalization.add_to(Email("noizwaves@example.com"))
personalization.add_cc(Email("seasea@example.com", "Cece"))
personalization.add_bcc(Email("beeseac@example.com", "Beeseac"))
mail.add_personalization(personalization)

if sandbox:
    mail.mail_settings = MailSettings()
    mail.mail_settings.sandbox_mode = SandBoxMode(enable=True)

try:
    response = sg.client.mail.send.post(request_body=mail.get())
    print(response.status_code)
    print(response.body)
    print(response.headers)
except python_http_client.exceptions.HTTPError as e:
    print("HTTPError")
    print(e.to_dict)

