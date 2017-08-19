import Json.Decode as Decode
import Json.Decode.Extra exposing ((|:))
import Html exposing (Html, div, text, dd, dt, dl, hr, span, ul, li)
import Html.Attributes exposing (class)
import Html.Events exposing (onClick)
import Http
import RemoteData exposing (sendRequest, RemoteData(..), WebData)
import WebSocket

type Msg
  = HelloWorld
  | UpdateEmails (WebData EmailList)
  | SelectEmail EmailId
  | UpdateEmail (WebData Email)
  | WSEmailsMessage String


type alias Model =
  { emails: WebData EmailList
  , email: WebData Email
  }


type alias EmailId = Int

type alias EmailListItem =
    { id: EmailId
    , to: String
    , from: String
    , subject: String
    }

type alias Email =
    { id: EmailId
    , to: String
    , from: String
    , subject: String
    , body: String
    }

type alias EmailList = List EmailListItem


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
  case msg of
    HelloWorld ->
      ( model, Cmd.none )
    UpdateEmails response ->
      ( { model | emails = response }, Cmd.none )
    UpdateEmail response ->
      ( { model | email = response }, Cmd.none )
    SelectEmail id ->
      ( model, fetchEmail id )
    WSEmailsMessage newEmailStr ->
      let
        newEmailResult = Decode.decodeString decodeEmailListItem newEmailStr
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

decodeEmails : Decode.Decoder EmailList
decodeEmails =
  Decode.list decodeEmailListItem

decodeEmailListItem : Decode.Decoder EmailListItem
decodeEmailListItem =
  Decode.succeed EmailListItem
    |: (Decode.field "id" Decode.int)
    |: (Decode.field "to" Decode.string)
    |: (Decode.field "from" Decode.string)
    |: (Decode.field "subject" Decode.string)

decodeEmail : Decode.Decoder Email
decodeEmail =
  Decode.succeed Email
    |: (Decode.field "id" Decode.int)
    |: (Decode.field "to" Decode.string)
    |: (Decode.field "from" Decode.string)
    |: (Decode.field "subject" Decode.string)
    |: (Decode.field "body" Decode.string)

fetchEmailList : Cmd Msg
fetchEmailList =
  Http.request
    { method = "GET"
    , headers = [ ]
    , url = "/api/emails"
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

viewEmailListItem : EmailListItem -> Html Msg
viewEmailListItem email =
  div [ class "email", onClick (SelectEmail email.id) ]
    [ div [] [ text "To: ", text email.to ]
    , div [] [ text "From: ", text email.from ]
    , div [] [ text "Subject: ", text email.subject ]
    ]

view : Model -> Html Msg
view model =
  let
    emails = case model.emails of
      Success emailsList ->
        List.map viewEmailListItem emailsList
      Loading ->
        [ div [] [ text "Loading..." ] ]
      _ ->
        [ div [] [] ]
    emailList = [ div [ class "email-list" ] emails ]
    emailDetail = case model.email of
      NotAsked ->
        []
      Loading ->
        [ div [ class "email-detail" ] [ text "Loading..." ] ]
      Success email ->
        [ div [ class "email-detail" ]
          [ dl []
            [ dt [] [ text "From:" ]
            , dd [] [ text email.from ]
            , dt [] [ text "To:" ]
            , dd [] [ text email.to ]
            , dt [] [ text "Subject:" ]
            , dd [] [ text email.subject ]
            , hr [] []
            , div [ class "email-detail--body" ] [ text email.body ]
            ]
          ]
        ]
      Failure err ->
        [ div [ class "email-detail" ] [ text "Error" ] ]
  in

    div []
      [ div [ class "nav-bar" ]
        [ div [ class "container container__nav" ]
          [ div [ class "logo" ] [ text "Wallraff" ]
          , ul [ class "nav" ]
            [ li [ class "nav-item" ] [ text "Emails"]
            ]
          ]
        ]
      , div [ class "page-content" ]
          [ div [ class "email-pane" ] (emailList ++ emailDetail)
          ]
      ]



subscriptions : Model -> Sub Msg
subscriptions model =
  WebSocket.listen "ws://localhost:3000/wsapi/emails" WSEmailsMessage

init : ( Model, Cmd Msg )
init = ( Model Loading NotAsked, fetchEmailList )

main : Program Never Model Msg
main =
  Html.program { init = init, view = view, update = update, subscriptions = subscriptions }