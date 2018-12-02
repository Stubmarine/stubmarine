module SendGrid.App

open Suave
open Suave.Filters
open Suave.Operators
open Suave.Json
open SendGrid
open SendGrid.Domain
open SendGrid.FormDto
open SendGrid.ViewDto
open System.Runtime.Serialization

// Persistence
type SelectAllEmailMessages = unit -> EmailMessage list

type AddEmailMessages = EmailMessage list -> unit

// Flows
let private deliverMailSend (addEmails : AddEmailMessages) (request : HttpRequest) : WebPart =
    fromJson<MailSendForm> request.rawForm
    |> FormDto.MailSend.toDomain
    |> deliver
    |> addEmails
    ""
    |> Successful.ACCEPTED
    >=> Writers.setMimeType "text/plain; charset=utf-8"

let private listEmails (selectEmails : SelectAllEmailMessages) (request : HttpRequest) : WebPart =
    selectEmails ()
    |> EmailMessage.toEmailListView
    |> toJson
    |> Successful.ok
    >=> Writers.setMimeType "application/json"

// Error DTOs
[<DataContract>]
type private ErrorView =
    { [<field:DataMember(Name = "message")>]
      message : string
      [<field:DataMember(Name = "field")>]
      field : string
      [<field:DataMember(Name = "help")>]
      help : string }

[<DataContract>]
type private ErrorsView =
    { [<field:DataMember(Name = "errors")>]
      errors : ErrorView [] }

let private toErrors (message : string) : ErrorsView =
    let error =
        { message = message
          field = Unchecked.defaultof<string>
          help = Unchecked.defaultof<string> }
    { errors = [| error |] }

let private errorToJson (message : string) : byte [] =
    message
    |> toErrors
    |> toJson

let private protect (validToken : string) (protectedPart : WebPart) (x : HttpContext) : Async<HttpContext option> =
    async { 
        let validValue = sprintf "Bearer %s" validToken
        let authHeader = x.request.header "Authorization"
        
        let authErrorMessage =
            match authHeader with
            | Choice1Of2 authHeader -> 
                if (String.equals authHeader validValue) then None
                else Some "The provided authorization grant is invalid, expired, or revoked"
            | Choice2Of2 _ -> Some "Permission denied, wrong credentials"
        
        let response =
            match authErrorMessage with
            | None -> protectedPart x
            | Some err -> RequestErrors.unauthorized (errorToJson err) x
        
        return! response
    }

let private clientUserAgent (x : HttpContext) : Async<HttpContext option> =
    async { 
        let userAgentHeader = x.request.header "User-agent"
        return match userAgentHeader with
               | Choice2Of2 _ -> None
               | Choice1Of2 userAgent -> 
                   if userAgent.StartsWith "sendgrid/" then (Some x)
                   else None
    }

let sendGridApp addEmails selectAllEmailMessages =
    let deliverHandler = deliverMailSend addEmails
    let listHandler = listEmails selectAllEmailMessages
    choose [ POST >=> clientUserAgent >=> path "/v3/mail/send" >=> protect "1234" (request deliverHandler)
             GET >=> path "/api/sendgrid/emails" >=> request listHandler ]
