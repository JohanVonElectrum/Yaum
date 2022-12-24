# TODO
## Core
- Rule: Universal server rules.
  - ex. `requireClient`
- UserRule: per user value
  - Not all rules are UserRules.
  - ex. `preferedLang`
- ClientRule: client side rules
  - ex. `useProtocol["always", "required", "never"]`
- ClientServerRule: client side rules per server
  - ex. `useProtocolOverride["always", "required", "never"]`
- Per user translations
  - Server-side when Yaum protocol is not enabled between user and server (using `lang` user rule).
  - Client-side when the translation must exist on the client-side.

## Should FIX
- `Badly compressed packet - size of 2 is below server threshold of 256` with `online-mod=false`. Disable compression if you have `online-mode=false`.

## Rules
- CreativeKill default behaviour with item frames.
- CreativeKill should kill falling sands.
- No max velocity.
- Dispensers place items in item frames.
- Bed fallback.
- Disable baby with spawn egg.
- 5 mins despawn xp orb.

## Commands
- Lang manager:
  - List installed langs.
  - List loaded langs.
  - Load langs.
  - Unload langs.