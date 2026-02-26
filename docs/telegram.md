# Telegram Mini Apps — практичная шпаргалка

## Purpose
Этот файл фиксирует инженерные правила для Mini App в нашем проекте: как безопасно обрабатывать авторизацию, как верстать интерфейс внутри контейнера Telegram и как избегать типовых багов.
Mini App — это web-приложение внутри Telegram-клиента, а не отдельный протокол взаимодействия с серверами Telegram.
Bot API и проверка `initData` закрывают наш основной backend-сценарий аутентификации.
`core.telegram.org/api`, `schema` и `mtproto` описывают Telegram API/MTProto-уровень, который нужен для клиентов Telegram и низкоуровневой интеграции, но не для нашего Mini App flow.
Практическая цель документа: единый набор MUST/SHOULD правил для frontend и backend.
Использовать этот файл как чек-лист перед merge, чтобы не ломать авторизацию, layout и тему.

## Key concepts (Mini Apps)
- **`initData` vs `initDataUnsafe`**: доверять только raw `initData`, переданному на backend для криптографической проверки; `initDataUnsafe` считать helper-данными UI, но не источником доверия.
- **`themeParams` / `colorScheme`**: Telegram передаёт параметры темы и светлую/тёмную схему; UI должен подстраиваться динамически.
- **`safeAreaInset` vs `contentSafeAreaInset`**: это разные inset-слои (системные vs контентные под Telegram UI), их нельзя смешивать без понимания назначения.
- **`viewport` events**: изменение высоты viewport (в т.ч. из-за клавиатуры) должно обрабатываться через события Telegram WebApp, а не через статичный `vh`.

## Security model (MUST)
- `initData` всегда недоверенный ввод, пока backend не завершил проверку подписи.
- Валидация `initData` обязательна по алгоритму Telegram: собрать `data-check-string`, вычислить HMAC-SHA-256, сравнить hash в constant-time, проверить свежесть `auth_date` по TTL.
- Нельзя логировать `initData` целиком (включая `hash`, `user`, `auth_date`); при диагностике логировать только безопасные минимальные поля.
- Контракт статусов ошибок:
  - `401` — `initData` отсутствует или `auth_date` истёк,
  - `403` — `hash mismatch` (невалидная подпись),
  - `400` — плохой формат (`hash` отсутствует, decode/query parse ошибка).

## Safe area & layout (Frontend MUST)
- Разница inset:
  - `safeAreaInset` — системные отступы устройства (notch, home indicator),
  - `contentSafeAreaInset` — контентная область внутри Telegram UI (учитывает элементы оболочки Telegram).
- Использовать CSS variables Telegram:
  - `--tg-safe-area-inset-top`, `--tg-safe-area-inset-bottom`, `--tg-safe-area-inset-left`, `--tg-safe-area-inset-right`;
  - `--tg-content-safe-area-inset-top`, `--tg-content-safe-area-inset-bottom`, `--tg-content-safe-area-inset-left`, `--tg-content-safe-area-inset-right`.
- Практика применения:
  - `AppLayout`: `padding-top/bottom` от **content safe area**,
  - sticky bottom элементы (CTA, TabBar): дополнительный `padding-bottom` от **safe area**.
- Не использовать `100vh` в корневых контейнерах: на мобильных/встроенных webview `vh` часто не соответствует фактической видимой высоте и ломает экран при появлении клавиатуры.
- Использовать `100dvh` + реакцию на `viewportChanged` для корректной адаптации высоты.

## Theme & palette (Frontend SHOULD)
- Базироваться на `themeParams`: не хардкодить ключевые цвета интерфейса.
- Обновлять тему на событие `themeChanged`, чтобы UI не рассинхронизировался с Telegram.
- Архитектура токенов: **base palette → semantic tokens → component tokens**.
- Под пользовательскую палитру предусматривать ограниченный override (в первую очередь `primary/accent`), не ломая контраст и читаемость.

## Telegram APIs and what we do NOT use
- `core.telegram.org/api`, `core.telegram.org/schema`, `core.telegram.org/mtproto` — документация Telegram API/MTProto уровня.
- Для Mini App в этом проекте MTProto не нужен: мы не строим Telegram-клиент и не работаем на уровне протокола.
- Мы используем bot token на backend только для проверки подписи `initData` по алгоритму Web Apps.
- Прямой запрет: **не писать MTProto-клиент в рамках этого проекта**.

## Practical checklists

### Frontend checklist
- [ ] `initData` отправляется на backend во всех реальных (`GET/POST/PUT/PATCH/DELETE`) запросах.
- [ ] safe area учтён (`contentSafeAreaInset` для layout, `safeAreaInset` для нижних sticky-элементов).
- [ ] `themeParams` и `themeChanged` обработаны.
- [ ] В корневых контейнерах нет `100vh`; используется `100dvh`/`viewportChanged`.

### Backend checklist
- [ ] `OPTIONS` разрешён без auth/initData-проверки.
- [ ] `initData` валидируется строго по документации Telegram (`data-check-string` + HMAC).
- [ ] Проверяется TTL для `auth_date`.
- [ ] Сравнение hash выполняется в constant-time.
- [ ] `initData` не логируется целиком.

## Links (official)
- https://core.telegram.org/bots/webapps#contentsafeareainset
- https://core.telegram.org/bots/webapps
- https://core.telegram.org/api
- https://core.telegram.org/schema
- https://core.telegram.org/mtproto
