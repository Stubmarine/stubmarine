module SendGrid

open Suave
open Suave.Filters
open Suave.Operators
open Suave.Json
open System.Runtime.Serialization

// DTOs (SendGrid API)
[<DataContract>]
type private AddressForm =
    { [<field:DataMember(Name = "email")>]
      email : string
      [<field:DataMember(Name = "name", IsRequired = false)>]
      name : string }

[<DataContract>]
type private PersonalizationForm =
    { [<field:DataMember(Name = "to", IsRequired = false)>]
      to_ : AddressForm []
      [<field:DataMember(Name = "cc", IsRequired = false)>]
      cc : AddressForm []
      [<field:DataMember(Name = "bcc", IsRequired = false)>]
      bcc : AddressForm [] }

[<DataContract>]
type private ContentForm =
    { [<field:DataMember(Name = "type")>]
      type_ : string
      [<field:DataMember(Name = "value")>]
      value : string }

[<DataContract>]
type private MailSendForm =
    { [<field:DataMember(Name = "subject")>]
      subject : string
      [<field:DataMember(Name = "from")>]
      from : AddressForm
      [<field:DataMember(Name = "personalizations")>]
      personalizations : PersonalizationForm []
      [<field:DataMember(Name = "content")>]
      content : ContentForm [] }

// DTOs (Stubmarine API)
[<DataContract>]
type private RecipientView =
    { [<field:DataMember(Name = "email")>]
      email : string
      [<field:DataMember(Name = "name")>]
      name : string }

[<DataContract>]
type private EmailView =
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
type private EmailListView = EmailView []

// Domain
type private Email = string

type private Name = string

type private Address =
    | EmailOnly of Email
    | EmailAndName of Email * Name

type private Content =
    { type_ : string
      value : string }

type private Personalization =
    { to_ : Address list
      cc : Address list
      bcc : Address list }

type private MailSend =
    { from : Address
      subject : string
      personalizations : Personalization list
      content : Content list }

type private EmailMessage =
    { from : Address
      to_ : Address list
      cc : Address list
      bcc : Address list
      subject : string
      content : Content list }

let private deliver (mailSend : MailSend) : EmailMessage list =
    let toMessage (personalization : Personalization) : EmailMessage =
        { from = mailSend.from
          to_ = personalization.to_
          cc = personalization.cc
          bcc = personalization.bcc
          subject = mailSend.subject
          content = mailSend.content }
    List.map toMessage mailSend.personalizations

// DTO > Domain
let private toAddress (address : AddressForm) : Address =
    match address.name with
    | null -> EmailOnly address.email
    | name -> EmailAndName(address.email, name)

let private toContent (content : ContentForm) : Content =
    { type_ = content.type_
      value = content.value }

let private toPersonalization (personalization : PersonalizationForm) : Personalization =
    let to_ =
        match personalization.to_ with
        | null -> List.empty
        | tos -> 
            (tos
             |> Array.toList
             |> List.map toAddress)
    
    let cc =
        match personalization.cc with
        | null -> List.empty
        | tos -> 
            (tos
             |> Array.toList
             |> List.map toAddress)
    
    let bcc =
        match personalization.bcc with
        | null -> List.empty
        | tos -> 
            (tos
             |> Array.toList
             |> List.map toAddress)
    
    { to_ = to_
      cc = cc
      bcc = bcc }

let private toMailSend (form : MailSendForm) : MailSend =
    let personalizations =
        form.personalizations
        |> Array.toList
        |> List.map toPersonalization
    
    let content =
        form.content
        |> Array.toList
        |> List.map toContent
    
    { from = toAddress form.from
      subject = form.subject
      personalizations = personalizations
      content = content }

// Domain > DTO
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

let private toEmailListView (emails : EmailMessage list) : EmailListView =
    emails
    |> List.map toEmailView
    |> List.toArray

// console outputting
let private sprintAddress (address : Address) : string =
    match address with
    | EmailOnly email -> email
    | EmailAndName(email, name) -> sprintf "%s <%s>" name email

let private printfRecipients (label : string) (addresses : Address list) : Unit =
    match addresses with
    | [] -> ()
    | _ -> 
        addresses
        |> List.map sprintAddress
        |> List.reduce (fun s1 s2 -> s1 + ", " + s2)
        |> printfn "%s: %s" label

let private printfContent (content : Content) : Unit =
    printfn "----------"
    printfn "Content Type: %s" content.type_
    printfn "%s" content.value
    printfn "----------"

let private printfEmailMessage (message : EmailMessage) : Unit =
    match message.from with
    | EmailOnly email -> printfn "From: %s" email
    | EmailAndName(email, name) -> printfn "From: %s <%s>" name email
    printfRecipients "To" message.to_
    printfRecipients "Cc" message.cc
    printfRecipients "Bcc" message.bcc
    printfn "Subject: %s" message.subject
    List.iter printfContent message.content
    printfn ""

// infrastructure
let private store = ResizeArray<EmailMessage>()

let private addEmails (items : EmailMessage list) : EmailMessage list =
    items |> List.iter store.Add
    items

// flows
let private printEmailAndOk (form : MailSendForm) : string =
    form
    |> toMailSend
    |> deliver
    |> addEmails
    |> List.iter printfEmailMessage
    "sent"

let private listEmails (request) : WebPart =
    store.ToArray()
    |> Array.toList
    |> toEmailListView
    |> toJson
    |> Successful.ok
    >=> Writers.setMimeType "application/json"

let sendGridApp =
    choose [ POST >=> path "/v3/mail/send" >=> mapJson printEmailAndOk
             GET >=> path "/api/sendgrid/emails" >=> request listEmails ]