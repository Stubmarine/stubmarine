module View exposing (view)

import Html exposing (Html, a, div, text, dd, dt, dl, h3, hr, span, ul, li)
import Html.Attributes exposing (class, classList)
import Html.Events exposing (onClick)

import RemoteData exposing (RemoteData(..))

import Model exposing (..)
import Message exposing (..)

viewEmailListItem : Email -> Html Msg
viewEmailListItem email =
  div [ class "email", onClick (SelectEmail email.id) ]
    [ div [] [ text "From: ", text email.from ]
    , div [] [ text "To: ", text email.to ]
    , div [] [ text "Subject: ", text email.subject ]
    ]

viewEmailPage : Model -> Html Msg
viewEmailPage model =
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
    div [ class "page-content" ]
      [ div [ class "email-pane" ] (emailList ++ emailDetail)
      ]

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
    div [ class "page-content" ]
      [ div [ class "endpoint-pane" ]
        [ div [ class "endpoint-list" ] content
        ]
      ]

viewNavItem : Route -> Route -> Html Msg
viewNavItem current target =
  let
    label = case target of
      Emails ->
        "Emails"
      Endpoints ->
        "Endpoints"
    classes =
      [ ("nav-item", True)
      , ("nav-item__active", target == current)
      ]
  in
    li [ classList classes ] [ a [ onClick (ChangeRoute target) ] [ text label ]]

view : Model -> Html Msg
view model =
  let
    pageContent = case model.route of
      Emails ->
        viewEmailPage model
      Endpoints ->
        viewEndpointsPage model

    navItem = viewNavItem model.route
  in
    div []
      [ div [ class "nav-bar" ]
        [ div [ class "container container__nav" ]
          [ div [ class "logo" ] [ text "Wallraff" ]
          , ul [ class "nav" ]
            [ navItem Emails
            , navItem Endpoints
            ]
          ]
        ]
      , pageContent
      ]