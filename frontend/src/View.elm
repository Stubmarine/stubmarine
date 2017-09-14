module View exposing (view)

import Html exposing (Html, a, button, div, em ,form, input, text, dd, dt, dl, h3, hr, span, ul, li)
import Html.Attributes exposing (class, classList, defaultValue, disabled, id, placeholder, type_)
import Html.Events exposing (onClick, onInput, onSubmit)

import RemoteData exposing (RemoteData(..))

import Model exposing (..)
import Message exposing (..)

viewLandingPage : Model -> Html Msg
viewLandingPage model =
  let
    isValid = isValidInboxName model.inboxName

    formAttrs = if isValid then
        [ class "form", onSubmit (SelectInbox model.inboxName) ]
      else
        [ class "form" ]
  in
    div [ class "page-content" ]
      [ div [ class "inboxes-pane" ]
        [ div [ class "inbox-create" ]
          [ span [ class "instruction" ] [ text "Choose an inbox" ]
          , form formAttrs
            [ input [ id "inbox-name", type_ "text", defaultValue model.inboxName, placeholder "Enter an inbox name", onInput FormInputInboxName ] []
            , button [ class "button-secondary", type_ "button", onClick GenerateRandomInboxName ] [ text "Suggest Random Name" ]
            , button [ class "button-primary", type_ "submit", disabled (not isValid) ] [ text "Continue" ]
            ]
          ]
        ]
      ]

viewEmailListItem : Email -> Html Msg
viewEmailListItem email =
  let
    fromTo =
      [ div [] [ text "From: ", text email.from ]
      , div [] [ text "To: ", text email.to ]
      ]
    cc = if email.cc == "" then
        [ ]
      else
        [ div [] [ text "Cc: ", text email.cc ] ]
    bcc = if email.bcc == "" then
        [ ]
      else
        [ div [] [ text "Bcc: ", text email.bcc ] ]
    subject = [ div [] [ text "Subject: ", text email.subject ] ]
  in
    div [ class "email", onClick (SelectEmail email.id) ] (fromTo ++ cc ++ bcc ++ subject)

viewEmailsPage : Model -> Html Msg
viewEmailsPage model =
  let
    emails = case model.emails of
      Success emailsList ->
        case emailsList of
          [] ->
            [ div [ class "alert-warning" ]
              [ em [] [ text "Inbox is empty." ]
              , text "Send an email to one of the endpoints above and it will appear here."
              ]
            ]
          _ -> List.map viewEmailListItem emailsList
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
        [ viewEmail email ]
      Failure err ->
        [ div [ class "email-detail" ] [ text "Error" ] ]
  in
    div [ class "email-pane" ] (emailList ++ emailDetail)

viewEmail : Email -> Html Msg
viewEmail email =
  let
    from =
      [ dt [] [ text "From:" ]
      , dd [] [ text email.from ]
      ]
    to =
      [ dt [] [ text "To:" ]
      , dd [] [ text email.to ]
      ]
    cc = if email.cc == "" then
        []
      else
        [ dt [] [ text "Cc:" ]
        , dd [] [ text email.cc ]
        ]
    bcc = if email.bcc == "" then
        []
      else
        [ dt [] [ text "Bcc:" ]
        , dd [] [ text email.bcc ]
        ]
    subject =
      [ dt [] [ text "Subject:" ]
      , dd [] [ text email.subject ]
      ]
    body =
      [ hr [] []
      , div [ class "email-detail--body" ] [ text email.body ]
      ]
  in
    div [ class "email-detail" ]
      [ dl [] ( from ++ to ++ cc ++ bcc ++ subject ++ body ) ]

viewEndpoint : Endpoint -> Html Msg
viewEndpoint endpoint =
  div [ class "endpoint" ]
    [ h3 [] [ text endpoint.name ]
    , div []
      [ span [ class "endpoint-desc" ] [ text "Originally (and in production) your host is configured as..." ]
      , span [ class "endpoint-url"] [ text endpoint.originalHost ]
      ]
    , div []
      [ span [ class "endpoint-desc" ] [ text "Configure your non-production environments to use..." ]
      , span [ class "endpoint-url" ] [ text endpoint.newHost ]
      ]
    , div []
      [ span [ class "endpoint-desc" ] [ text "Originally (and in production) your token is configured as..." ]
      , span [ class "endpoint-url"] [ text "<some random characters>" ]
      ]
    , div []
      [ span [ class "endpoint-desc" ] [ text "Configure your non-production environments to use..." ]
      , span [ class "endpoint-url", class "token-value" ] [ text endpoint.newToken ]
      ]
    , div []
      [ span [ class "endpoint-desc" ] [ text "An example usage would look like this..." ]
      , span [ class "endpoint-url" ] [ text endpoint.example ]
      ]
    ]

viewEndpointsPage : Model -> Html Msg
viewEndpointsPage model =
  let
    content = case model.endpoints of
      Success endpoints ->
        List.map viewEndpoint endpoints
      Loading ->
        [ div [] [ text "Loading..." ] ]
      _ ->
        []
  in
    div [ class "endpoint-pane" ]
      [ div [ class "endpoint-list" ] content
      ]

viewInbox : String -> InboxRoute -> Model -> Html Msg
viewInbox inboxName subRoute model =
  let
    content = case subRoute of
      Emails ->
        viewEmailsPage model
      Endpoints ->
        viewEndpointsPage model

    navLinkClasses = \target -> classList
      [ ("nav-link", True)
      , ("nav-link__active", target == subRoute)
      ]

    pageNav = div [ class "page-content--nav" ]
      [ ul [ class "nav" ]
        [ li [ class "nav-item" ]
          [ span [ class "nav-link" ] [ text inboxName ]
          ]
        , li [ class "nav-item" ]
          [ a [ navLinkClasses Emails, onClick (ChangeRoute (Inbox inboxName Emails)) ] [ text "Emails" ]
          ]
        , li [ class "nav-item" ]
          [ a [ navLinkClasses Endpoints, onClick (ChangeRoute (Inbox inboxName Endpoints)) ] [ text "Endpoints" ]
          ]
        ]
      ]
  in
    div [ class "page-content" ]
    [ pageNav
    , content
    ]

view : Model -> Html Msg
view model =
  let
    pageContent = case model.route of
      Landing ->
        viewLandingPage model
      Inbox inboxName subRoute ->
        viewInbox inboxName subRoute model
  in
    div []
      [ div [ class "nav-bar" ]
        [ div [ class "container container__nav" ]
          [ div [ class "logo", onClick (ChangeRoute Landing) ] [ text "Wallraff" ]
          , ul [ class "nav" ] [ ]
          ]
        ]
      , pageContent
      ]