import sys

import sendgrid
from sendgrid.helpers.mail import *

argv = sys.argv
if len(argv) not in [2, 3]:
    print('Usage: python send_email_via_sendgrid.py <token> [production]')
    exit(1)

if len(argv) == 3 and argv[2] == "production":
    host = "https://wallraff.cfapps.io"
else:
    host = "http://localhost:8080"

token = argv[1]
if token is None or token == '':
    print("!!! Error: token should be a non-empty valid string!")
    exit(1)

print("Sending SendGrid email to " + host)

sg = sendgrid.SendGridAPIClient(apikey=token, host=host + "/eapi/sendgrid")
from_email = Email("test@example.com")
to_email = Email("adam@example.com", "Adam")
subject = "Sending with SendGrid is Fun"
content = Content("text/plain", "and easy to do anywhere, even with Python")
mail = Mail(from_email, subject, to_email, content)

personalization = Personalization()
personalization.add_to(Email("noizwaves@example.com"))
# personalization.add_cc(Email("seasea@example.com", "Cece"))
# personalization.add_bcc(Email("beeseac@example.com", "Beeseac"))
mail.add_personalization(personalization)

response = sg.client.mail.send.post(request_body=mail.get())
print(response.status_code)
print(response.body)
print(response.headers)
