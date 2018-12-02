module SendGrid.Persistence

open SendGrid.Domain

let private store = ResizeArray<Domain.EmailMessage>()
let getEmails = fun () -> store.ToArray() |> Array.toList
let saveEmails (items : Domain.EmailMessage list) = items |> List.iter store.Add
