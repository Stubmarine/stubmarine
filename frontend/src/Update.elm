module Update exposing (..)

import Array exposing (fromList)
import Char
import List
import Json.Decode as Decode
import Json.Decode.Extra exposing ((|:))
import Http
import Random
import RemoteData exposing (sendRequest, RemoteData(..))

import Message exposing (Msg, Msg(..))
import Model exposing (..)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    FormInputInboxName inboxName ->
      ( { model | inboxName = inboxName }, Cmd.none )
    GenerateRandomInboxName ->
      ( model, Random.generate FormInputInboxName randomInboxName )

    ChangeRoute target ->
      ( { model | route = target }, Cmd.none )

    SelectInbox inboxName ->
      ( model, Cmd.none )
      -- Fetch emails
        |> \(m, c) -> ( { m | emails = Loading, email = NotAsked }, Cmd.batch [c, fetchEmailList inboxName] )
      -- Fetch endpoints
        |> \(m, c) -> ( { m | endpoints = Loading }, Cmd.batch [c, fetchEndpointList inboxName] )
      -- change route
        |> \(m, c) -> ( { m | route = Inbox inboxName Emails }, c )

    UpdateEmails response ->
      ( { model | emails = response }, Cmd.none )

    SelectEmail id ->
      ( model, fetchEmail id )
    UpdateEmail response ->
      ( { model | email = response }, Cmd.none )

    UpdateEndpoints response ->
      ( { model | endpoints = response }, Cmd.none )

    WSEmailsMessage newEmailStr ->
      let
        newEmailResult = Decode.decodeString decodeEmail newEmailStr
        existingEmails = case model.emails of
          Success existing ->
            existing
          _ ->
            []
      in
        case newEmailResult of
          Ok newEmail ->
            ( { model | emails = (Success (existingEmails ++ [newEmail])) }, Cmd.none )
          _ ->
            ( model, Cmd.none )

randomStringArrayItem : Array.Array String -> Random.Generator String
randomStringArrayItem items =
  let
    index = Random.int 1 ((Array.length items) - 1)
    getAtIndex = \i -> Maybe.withDefault "" (Array.get i items)
  in
    Random.map getAtIndex index

randomInboxName : Random.Generator String
randomInboxName =
  let
    adjectives = fromList
      [ "purple"
      , "green"
      , "blue"
      , "lively"
      , "aching"
      , "tired"
      , "silky"
      , "woeful"
      ]

    nouns = fromList
      [ "puppy"
      , "kitten"
      , "elephant"
      , "seal"
      , "lizard"
      , "hippo"
      , "squirrel"
      ]

    adjective = randomStringArrayItem adjectives
    noun = randomStringArrayItem nouns

    lowerCaseCodes = List.range (Char.toCode 'a') (Char.toCode 'z')
    upperCaseCodes = List.range (Char.toCode 'A') (Char.toCode 'Z')
    digitCodes = List.range (Char.toCode '0') (Char.toCode '9')

    alphaNumeric = List.concat [ lowerCaseCodes, upperCaseCodes, digitCodes ]
      |> List.map Char.fromCode
      |> List.map String.fromChar
      |> Array.fromList
      |> randomStringArrayItem

    alphaNumericString = alphaNumeric
      |> Random.list 4
      |> Random.map String.concat
  in
    Random.map3 (\s t u -> s ++ "-" ++ t ++ "-" ++ u) adjective noun alphaNumericString

decodeEmails : Decode.Decoder EmailList
decodeEmails =
  Decode.list decodeEmail

decodeEmail : Decode.Decoder Email
decodeEmail =
  Decode.succeed Email
    |: (Decode.field "id" Decode.int)
    |: (Decode.field "to" Decode.string)
    |: (Decode.field "from" Decode.string)
    |: (Decode.field "subject" Decode.string)
    |: (Decode.field "body" Decode.string)

fetchEmailList : InboxName -> Cmd Msg
fetchEmailList inboxName =
  Http.request
    { method = "GET"
    , headers = [ ]
    , url = "/api/inbox/" ++ inboxName ++ "/emails"
    , body = Http.emptyBody
    , expect = Http.expectJson decodeEmails
    , timeout = Nothing
    , withCredentials = False
    }
    |> RemoteData.sendRequest
    |> Cmd.map UpdateEmails

fetchEmail : EmailId -> Cmd Msg
fetchEmail id =
  Http.request
    { method = "GET"
    , headers = [ ]
    , url = "/api/emails/" ++ (toString id)
    , body = Http.emptyBody
    , expect = Http.expectJson decodeEmail
    , timeout = Nothing
    , withCredentials = False
    }
    |> RemoteData.sendRequest
    |> Cmd.map UpdateEmail

decodeEndpoints : Decode.Decoder EndpointList
decodeEndpoints =
  Decode.list decodeEndpoint

decodeEndpoint : Decode.Decoder Endpoint
decodeEndpoint =
  Decode.succeed Endpoint
    |: (Decode.field "id" Decode.string)
    |: (Decode.field "name" Decode.string)
    |: (Decode.field "originalHost" Decode.string)
    |: (Decode.field "newHost" Decode.string)
    |: (Decode.field "newToken" Decode.string)
    |: (Decode.field "example" Decode.string)

fetchEndpointList : InboxName -> Cmd Msg
fetchEndpointList inboxName =
  Http.request
    { method = "GET"
    , headers = [ ]
    , url = "/api/inbox/" ++ inboxName ++ "/endpoints"
    , body = Http.emptyBody
    , expect = Http.expectJson decodeEndpoints
    , timeout = Nothing
    , withCredentials = False
    }
    |> RemoteData.sendRequest
    |> Cmd.map UpdateEndpoints