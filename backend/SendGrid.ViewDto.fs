module SendGrid.ViewDto

open SendGrid.Domain
open System.Runtime.Serialization

[<DataContract>]
type RecipientView =
    { [<field:DataMember(Name = "email")>]
      email : string
      [<field:DataMember(Name = "name")>]
      name : string }

[<DataContract>]
type EmailView =
    { [<field:DataMember(Name = "from")>]
      from : RecipientView
      [<field:DataMember(Name = "to")>]
      to_ : RecipientView []
      [<field:DataMember(Name = "cc")>]
      cc : RecipientView []
      [<field:DataMember(Name = "bcc")>]
      bcc : RecipientView []
      [<field:DataMember(Name = "subject")>]
      subject : string
      [<field:DataMember(Name = "body")>]
      body : string }

[<DataContract>]
type EmailListView = EmailView []

module EmailMessage =
    let private toRecipientView (address : Address) : RecipientView =
        match address with
        | EmailOnly email -> 
            { email = email
              name = Unchecked.defaultof<string> }
        | EmailAndName(email, name) -> 
            { email = email
              name = name }
    
    let private toEmailView (email : EmailMessage) : EmailView =
        let toRecipients addresses =
            addresses
            |> List.map toRecipientView
            |> List.toArray
        
        let firstContent content =
            email.content
            |> List.first
            |> Option.bind (fun (c : Content) -> Some c.value)
            |> Option.orDefault (fun () -> "-- No body --")
        
        { from = email.from |> toRecipientView
          to_ = email.to_ |> toRecipients
          cc = email.cc |> toRecipients
          bcc = email.bcc |> toRecipients
          subject = email.subject
          body = email.content |> firstContent }
    
    let toEmailListView (emails : EmailMessage list) : EmailListView =
        emails
        |> List.map toEmailView
        |> List.toArray
