module Main exposing (main)

import Browser exposing (Document, document)
import Html exposing (Html, div, h3, header, span, text)
import Html.Attributes exposing (class)
import Http
import Json.Decode
import RemoteData exposing (RemoteData(..), WebData)


type Recipient
    = EmailOnly String
    | EmailAndName String String


type alias Email =
    { from : Recipient
    , subject : String
    , to : List Recipient
    , cc : List Recipient
    , bcc : List Recipient
    }


type alias Emails =
    List Email


type alias EmailsData =
    WebData Emails


type alias Model =
    { emails : EmailsData }


type Msg
    = EmailsResponse EmailsData


type alias Flags =
    {}


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { emails = NotAsked }, getEmails )


recipientDecoder : Json.Decode.Decoder Recipient
recipientDecoder =
    Json.Decode.oneOf
        [ Json.Decode.map2 EmailAndName
            (Json.Decode.field "email" Json.Decode.string)
            (Json.Decode.field "name" Json.Decode.string)
        , Json.Decode.map EmailOnly
            (Json.Decode.field "email" Json.Decode.string)
        ]


emailsDecoder : Json.Decode.Decoder Emails
emailsDecoder =
    Json.Decode.map5
        Email
        (Json.Decode.field "from" recipientDecoder)
        (Json.Decode.field "subject" Json.Decode.string)
        (Json.Decode.field "to" (Json.Decode.list recipientDecoder))
        (Json.Decode.field "cc" (Json.Decode.list recipientDecoder))
        (Json.Decode.field "bcc" (Json.Decode.list recipientDecoder))
        |> Json.Decode.list


getEmails : Cmd Msg
getEmails =
    Http.get "/api/sendgrid/emails" emailsDecoder
        |> RemoteData.sendRequest
        |> Cmd.map EmailsResponse


viewRecipient recipient =
    case recipient of
        EmailOnly emailAddress ->
            emailAddress

        EmailAndName _ name ->
            name


viewEmail : Email -> Html Msg
viewEmail email =
    let
        from =
            span [ class "from" ] [ viewRecipient email.from |> text ]

        subject =
            span [ class "subject" ] [ text email.subject ]

        recipients =
            List.concat [ email.to, email.cc, email.bcc ]
                |> List.map viewRecipient
                |> List.map (\r -> span [ class "recipient" ] [ text r ])
    in
    div [ class "email" ] (from :: subject :: recipients)


viewEmails : EmailsData -> Html Msg
viewEmails emails =
    case emails of
        NotAsked ->
            div [] []

        Loading ->
            div [] [ text "Loading..." ]

        Failure _ ->
            div [] [ text "Error occured" ]

        Success items ->
            let
                emailItems =
                    items |> List.map viewEmail
            in
            div [ class "sendgrid-emails" ]
                [ h3 [] [ text "SendGrid Emails" ]
                , div [ class "emails" ] emailItems
                ]


view : Model -> Document Msg
view model =
    { title = "Stubmarine"
    , body =
        [ header [] [ text "Stubmarine" ]
        , viewEmails model.emails
        ]
    }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        EmailsResponse data ->
            ( { model | emails = data }, Cmd.none )


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none


main =
    document { init = init, view = view, update = update, subscriptions = subscriptions }
