module SendGridTest

open NUnit.Framework
open FsUnit
open SendGrid
open Suave.Http

let private extractSome hc =
    match hc with
    | Some a -> a
    | None -> failwith "Expected 'Some'"

let private extractStatusCode hc = hc.response.status.code

let private withPath (path : string) (hc : HttpContext) : HttpContext =
    let req = { hc.request with rawPath = path }
    { hc with request = req }

let private withMethod (method : HttpMethod) (hc : HttpContext) : HttpContext =
    let req = { hc.request with rawMethod = method.ToString() }
    { hc with request = req }

let private withHeader (header : string * string) (hc : HttpContext) : HttpContext =
    let headers = header :: hc.request.headers
    let req = { hc.request with headers = headers }
    { hc with request = req }

let private withContent (content : string) (hc : HttpContext) : HttpContext =
    let asBytes = UTF8.bytes content
    let req = { hc.request with rawForm = asBytes }
    { hc with request = req } |> withHeader ("Content-type", "application/json")

let private post (path : string) : HttpContext =
    HttpContext.empty
    |> withMethod HttpMethod.POST
    |> withPath path

[<Test>]
let ``Happy path "mail send" call``() =
    post "/v3/mail/send"
    |> withContent """{
        "personalizations": [
            {
                "to": [
                    {
                        "email": "john@example.com"
                    }
                ]
            }
        ],
        "from": {
            "email": "from_address@example.com"
        },
        "subject": "Hello, World!",
        "content": [
            {
                "type": "text/plain",
                "value": "Hello, World!"
            }
        ]
    }"""
    |> withHeader ("Authorization", "Bearer 1234")
    |> withHeader ("User-agent", "sendgrid/3.0.0;java")
    |> sendGridApp
    |> Async.RunSynchronously
    |> extractSome
    |> extractStatusCode
    |> should equal 202

[<Test>]
let ``Not from a SendGrid client``() =
    post "/v3/mail/send"
    |> withContent """{
        "personalizations": [
            {
                "to": [
                    {
                        "email": "john@example.com"
                    }
                ]
            }
        ],
        "from": {
            "email": "from_address@example.com"
        },
        "subject": "Hello, World!",
        "content": [
            {
                "type": "text/plain",
                "value": "Hello, World!"
            }
        ]
    }"""
    |> withHeader ("Authorization", "Bearer 1234")
    |> withHeader ("User-agent", "Chrome")
    |> sendGridApp
    |> Async.RunSynchronously
    |> should equal None
