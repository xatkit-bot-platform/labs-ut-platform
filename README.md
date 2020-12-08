Xatkit Facebook Messenger Platform
=====

[![Wiki Badge](https://img.shields.io/badge/doc-wiki-blue)](https://github.com/xatkit-bot-platform/xatkit-facebook-messenger-platform/wiki)
[![Build Status](https://travis-ci.com/xatkit-bot-platform/xatkit-facebook-messenger-platform.svg?branch=master)](https://travis-ci.com/xatkit-bot-platform/xatkit-facebook-messenger-platform)  

Receive and send messages to [Facebook Messenger](https://www.messenger.com/).
This connector utilizes the [Messenger Platform API](https://developers.facebook.com/docs/messenger-platform).

## Providers

| Provider | Type | Context Parameters | Description |
| -------- | ---- | ------------------ | ----------- |
| MessengerIntentProvider | Intent, Event | - `title`: The displayed text of a postback button<br/> - `payload`: The payload of a postback button<br/> - `refferal.ref`: The refferal ref attribute<br/> - `refferal.source` The referral origin<br/> - `refferal.type`: The refferal type<br/> - `mids`: The id of a message as defined by a message delivery<br/> - `mid`: The id of a message as referred to by a reaction<br/> - `watermark`: The number of messages delivered<br/> - `emoji`: The Unicode character of the emoji in a reaction<br/> - `reaction`: The natural language interpretation of the emoji in a reaction<br/> - `raw_text`: The raw text of a message | Receive webhook events from the Facebook API and translate them into Xatkit-compatible intents or events. Most of the context parameters correspond to the json properties of Messenger's [Webhooks](https://developers.facebook.com/docs/messenger-platform/webhook). |

## MessengerIntentProvider Events

| Event | Parameters | Description |
| ----- | ---------- | ----------- |
| Message_Delivered | - `mids`: The id of the delivered message | Event sent when a message is delivered. **Note**: this event is only created when the `xatkit.messenger.handle_deliveries` is set to `true`. |
| Message_Read | - `watermark`: The number of messages delivered | Event sent when the user sees a message. **Note**: this event is only created when the `xatkit.messenger.handle_read` is set to `true`. |
| Message_React | - `mid`: The id of the message reacted to<br/> - `emoji`: The Unicode character of the emoji<br/> - `reaction`: The natural language interpretation of the emoji | Event sent when the user reacts to a message. **Note**: this event is only created when the `xatkit.messenger.handle_reactions` is set to `true`. |
| Message_Unreact | - `mid`: The id of the message the reaction was removed from | Event sent when a reaction is removed from a message. **Note**: this event is only created when the `xatkit.messenger.handle_reactions` is set to `true`. |
| Message_Postback | - `title`: The displayed text of a postback button<br/> - `payload`: The payload of a postback button<br/> - `xatkit.messenger.postback.referral.ref`: The refferal ref attribute<br/> - `xatkit.messenger.postback.referral.source` The referral origin<br/> - `xatkit.messenger.postback.referral.type`: The refferal type | Event sent when the user click on a postback button in a button template. |

## Actions

The user the message is sent to is recognized by an ID stored in the StateContext.
| Action | Parameters | Return | Return Type | Description |
| ------ | ---------- | ------ | ----------- | ----------- |
| MarkSeen | - `context` (**StateContext**): context of the state this action was used in | Response to the rest request to the API | MessengerResponse | Marks the user's last message as seen. |
| SendAction | - `context` (**StateContext**): context of the state this action was used in<br/> - `senderAction` (**SenderAction**): the action to send to the API | Response to the rest request to the API | MessengerResponse | Marks the user's last message as seen or turns the typing effect on or off. |
| UploadFile | - `context` (**StateContext**): context of the state this action was used in<br/> - `file` (**File**): the data object used to associate data about an uploaded file | Response to the rest request to the API | MessengerResponse | Uploads a file to Facebook and remembers its `attachmentId`. |
| SendFile | - `context` (**StateContext**): context of the state this action was used in<br/> - `attacmentId` (**String**): The attachmentId remembered from UploadFile<br/> - `attachmentType` (**Attachment.AttachmentType**): The type of the attachment object as defined in the API | Response to the rest request to the API | MessengerResponse | Attaches an uploaded file to an attachment message and sends it to the user. |
| SendFile | - `context` (**StateContext**): context of the state this action was used in<br/> - `file` (**File**): the data object used to associate file-data and information about its attachment form | Response to the rest request of the SendFile method | MessengerResponse | Convenience Method that uses the UploadFile method if the file doesn't have an `attachmentId` and then sends the file with the SendFile method. |
| Reply | - `context` (**StateContext**): context of the state this action was used in<br/> - `text` (**String**): text to send as a message | Response to the rest request to the API | MessengerResponse | Converts the text to a message and sends it to the user. |
| Reply | - `context` (**StateContext**): context of the state this action was used in<br/> - `text` (**String**): text to send as a message<br/> - `naturalize` (**Boolean**): used to turn on text naturalization | Response to the rest request to the API | MessengerResponse | Naturalized the text, then sends the message to the user. **Note**: You need to set `xatkit.messenger.naturalize_text` true for the naturalization to go true. |
| Reply | - `context` (**StateContext**): context of the state this action was used in<br/> - `message` (**Message**): the message data object as defined by the API | Response to the rest request to the API | MessengerResponse | Sends the message to the user. |
| Reply | - `context` (**StateContext**): context of the state this action was used in<br/> - `attachment` (**Attachment**) the attachment data object as defined by the API | Response to the rest request to the API | MessengerResponse | Attaches the attachment to a message and sends it to the user. |

## Options

The Messenger Platform supports the following configuration options.
| Key | Values | Description | Constraint |
| --- | ------ | ----------- | ---------- |
| `xatkit.messenger.verify_token` | String | The [Messenger](https://developers.facebook.com/docs/messenger-platform/getting-started/app-setup) token given to the API to connect to the bot | **Mandatory** |
| `xatkit.messenger.access_token` | String | The [Messenger](https://developers.facebook.com/docs/facebook-login/access-tokens/?locale=en_US) token used by the bot to connect to the API | **Mandatory** |
| `xatkit.messenger.app_secret` | String | The [Messenger](https://developers.facebook.com/docs/facebook-login/security/) encryption secret | **Mandatory** |
| `xatkit.messenger.handle_reactions` | Boolean | Generate events from when the user reacts to a message | **Optional** (default `false`) |
| `xatkit.messenger.handle_deliveries` | Boolean | Generate events when a message is delivered | **Optional** (default `false`) |
| `xatkit.messenger.handle_reads` | Boolean | Generate events when the user reads a message | **Optional** (default `false`) |
| `xatkit.messenger.intent_from_postback` | Boolean | Generate intents instead of events when the user interacts with the postback button | **Optional** (default `false`) |
| `xatkit.messenger.intent_from_reaction` | Boolean | Generate intents instead of events when the user reacts to a message |  **Optional** (default `false`) |
| `xatkit.messenger.use_reaction_text` | Boolean | Use natural language interpretations of emojis instead of emojis themselves when generating intents from reactions | **Optional** (default `false`) |
| `xatkit.messenger.use_title_text` | Boolean | Use the button titles instead of their payloads when generating intents from postbacks | **Optional** (default `false`) |
| `xatkit.messenger.auto_seen` | Boolean |  Automatically mark the user's messages as seen | **Optional** (default `false`) |
| `xatkit.messenger.naturalize_text` | Boolean | Naturalize text when using the method that naturalizes text | **Optional** (default `false`) |
