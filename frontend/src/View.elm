module View exposing (view)

import Html exposing (Html, a, button, div, form, input, text, dd, dt, dl, h3, hr, span, ul, li)
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
  div [ class "email", onClick (SelectEmail email.id) ]
    [ div [] [ text "From: ", text email.from ]
    , div [] [ text "To: ", text email.to ]
    , div [] [ text "Subject: ", text email.subject ]
    ]

viewEmailsPage : Model -> Html Msg
viewEmailsPage model =
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
    div [ class "email-pane" ] (emailList ++ emailDetail)

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