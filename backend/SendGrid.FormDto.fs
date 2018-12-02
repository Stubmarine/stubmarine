module SendGrid.FormDto

open System.Runtime.Serialization

[<DataContract>]
type AddressForm =
    { [<field:DataMember(Name = "email")>]
      email : string
      [<field:DataMember(Name = "name", IsRequired = false)>]
      name : string }

[<DataContract>]
type PersonalizationForm =
    { [<field:DataMember(Name = "to", IsRequired = false)>]
      to_ : AddressForm []
      [<field:DataMember(Name = "cc", IsRequired = false)>]
      cc : AddressForm []
      [<field:DataMember(Name = "bcc", IsRequired = false)>]
      bcc : AddressForm [] }

[<DataContract>]
type ContentForm =
    { [<field:DataMember(Name = "type")>]
      type_ : string
      [<field:DataMember(Name = "value")>]
      value : string }

[<DataContract>]
type MailSendForm =
    { [<field:DataMember(Name = "subject")>]
      subject : string
      [<field:DataMember(Name = "from")>]
      from : AddressForm
      [<field:DataMember(Name = "personalizations")>]
      personalizations : PersonalizationForm []
      [<field:DataMember(Name = "content")>]
      content : ContentForm [] }

module MailSend =
    let private toAddress (address : AddressForm) : SendGrid.Domain.Address =
        match address.name with
        | null -> SendGrid.Domain.Address.EmailOnly address.email
        | name -> SendGrid.Domain.Address.EmailAndName(address.email, name)
    
    let private toContent (content : ContentForm) : SendGrid.Domain.Content =
        { type_ = content.type_
          value = content.value }
    
    let private toPersonalization (personalization : PersonalizationForm) : SendGrid.Domain.Personalization =
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
    
    let toDomain (dto : MailSendForm) : SendGrid.Domain.MailSend =
        let personalizations =
            dto.personalizations
            |> Array.toList
            |> List.map toPersonalization
        
        let content =
            dto.content
            |> Array.toList
            |> List.map toContent
        
        { from = toAddress dto.from
          subject = dto.subject
          personalizations = personalizations
          content = content }
