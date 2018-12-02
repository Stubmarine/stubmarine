module SendGrid.Domain

// Domain
type Email = string

type Name = string

type Address =
    | EmailOnly of Email
    | EmailAndName of Email * Name

type Content =
    { type_ : string
      value : string }

type Personalization =
    { to_ : Address list
      cc : Address list
      bcc : Address list }

type MailSend =
    { from : Address
      subject : string
      personalizations : Personalization list
      content : Content list }

type EmailMessage =
    { from : Address
      to_ : Address list
      cc : Address list
      bcc : Address list
      subject : string
      content : Content list }

let deliver (mailSend : MailSend) : EmailMessage list =
    let toMessage (personalization : Personalization) : EmailMessage =
        { from = mailSend.from
          to_ = personalization.to_
          cc = personalization.cc
          bcc = personalization.bcc
          subject = mailSend.subject
          content = mailSend.content }
    List.map toMessage mailSend.personalizations
