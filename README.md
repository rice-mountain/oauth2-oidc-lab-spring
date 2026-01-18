# OAuth2.0 / OpenID Connect 検証ラボ (Spring Boot)

Spring Bootで構築した、OAuth2.0とOpenID Connect (OIDC) の動作を検証するための実験環境です。

## 概要

このラボでは、以下の2つのSpring Bootアプリケーションで構成される環境を提供します：

1. **Authorization Server（認可・認証サーバー）** - OAuth2.0認可サーバー / OpenID Provider
2. **Client Server（クライアントサーバー）** - OAuth2クライアント + Resource Server (API)

### 主な機能

- ✅ **OAuth2.0フロー** - Access Tokenのみを取得する標準的なOAuth2.0フロー
- ✅ **OIDCフロー** - ID Token（ユーザー識別情報）を含むOpenID Connectフロー
- ✅ **Authorization Code Grant** - 認可コードフロー
- ✅ **Resource Server API** - JWT検証によるAPIアクセス制御
- ✅ **Webベースの検証UI** - フローを切り替えて動作を確認できる画面
- 🔧 **拡張可能な設計** - PKCE、Refresh Tokenなど将来的な拡張に対応

## 技術スタック

- **Java**: 17
- **Spring Boot**: 3.2.1
- **Spring Security OAuth2 Authorization Server**: 1.2.1
- **ビルドツール**: Gradle 8.5 (Gradle Wrapper使用)
- **テンプレートエンジン**: Thymeleaf

## プロジェクト構成

```
oauth2-oidc-lab-spring/
├── authorization-server/     # 認可・認証サーバー (ポート: 9000)
│   ├── src/main/java/
│   │   └── com/example/oauth2lab/authserver/
│   │       ├── AuthorizationServerApplication.java
│   │       └── config/
│   │           └── SecurityConfig.java
│   └── src/main/resources/
│       └── application.properties
├── client-server/            # クライアントサーバー (ポート: 8081)
│   ├── src/main/java/
│   │   └── com/example/oauth2lab/client/
│   │       ├── ClientServerApplication.java
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── ResourceServerConfig.java
│   │       └── controller/
│   │           ├── HomeController.java
│   │           └── ApiController.java
│   └── src/main/resources/
│       ├── application.properties
│       └── templates/
│           ├── index.html
│           └── dashboard.html
├── build.gradle              # ルートビルド設定
├── settings.gradle           # マルチモジュール設定
└── gradlew                   # Gradle Wrapper
```

## セットアップ手順

### 前提条件

- Java 17以上がインストールされていること
- `JAVA_HOME`環境変数が設定されていること

### 1. リポジトリのクローン

```bash
git clone https://github.com/rice-mountain/oauth2-oidc-lab-spring.git
cd oauth2-oidc-lab-spring
```

### 2. ビルド

```bash
./gradlew build
```

## 起動手順

### 方法1: 個別に起動（推奨）

**ターミナル1 - Authorization Serverの起動:**

```bash
./gradlew :authorization-server:bootRun
```

**ターミナル2 - Client Serverの起動:**

```bash
./gradlew :client-server:bootRun
```

### 方法2: バックグラウンドで起動

```bash
# Authorization Server
./gradlew :authorization-server:bootRun &

# Client Server
./gradlew :client-server:bootRun &
```

### 起動確認

- **Authorization Server**: http://localhost:9000
- **Client Server**: http://localhost:8081

## 検証手順

### 1. Webブラウザでアクセス

```
http://localhost:8081
```

### 2. 認証フローの選択

ホーム画面で2つのフローから選択できます：

#### OAuth2.0フロー（OAuth2のみ）
- **ボタン**: 「OAuth2 フローでログイン」
- **取得トークン**: Access Token のみ
- **スコープ**: `read`, `write`
- **特徴**: ID Tokenなし、UserInfoエンドポイントからユーザー情報取得

#### OIDCフロー（OpenID Connect）
- **ボタン**: 「OIDC フローでログイン」
- **取得トークン**: Access Token + ID Token
- **スコープ**: `openid`, `profile`, `read`, `write`
- **特徴**: ID Token内にユーザー識別情報（Claims）が含まれる

### 3. ログイン

Authorization Serverのログイン画面が表示されます。

**テストアカウント:**
- ユーザー名: `user`
- パスワード: `password`

### 4. 認可の承認

スコープの承認画面で、要求されたスコープを承認してください。

### 5. ダッシュボードで確認

認証が成功すると、ダッシュボード画面に以下の情報が表示されます：

- ✅ **認証情報** - ユーザー名、使用したクライアント
- 🎫 **ID Token** - OIDC フローの場合のみ表示
- 📋 **ID Token Claims** - IDトークンに含まれるクレーム情報
- 🔑 **Access Token** - リソースサーバーアクセス用
- 👤 **ユーザー属性** - 取得したユーザー情報
- 🧪 **API テスト** - Resource Server APIの動作確認

### 6. Resource Server API のテスト

ダッシュボード画面の「Resource Server API テスト」セクションから、以下のAPIを試せます：

- **GET /api/user** - 認証ユーザー情報
- **GET /api/data** - 保護されたデータ

これらのAPIは、取得したAccess Tokenを使ってJWT検証により保護されています。

## OAuth2.0とOIDCの違い

### OAuth2.0フロー
```
1. Authorization Request → Authorization Server
2. User Login & Consent
3. Authorization Code → Client
4. Token Request → Authorization Server
5. Access Token ← Authorization Server
6. Access Token で Resource Serverにアクセス
```

### OIDCフロー
```
1. Authorization Request (scope: openid) → Authorization Server
2. User Login & Consent
3. Authorization Code → Client
4. Token Request → Authorization Server
5. Access Token + ID Token ← Authorization Server
   ↑ ID Tokenにユーザー識別情報が含まれる
6. Access Token で Resource Serverにアクセス
```

## フロー詳細

### Authorization Code Grant Flow

現在実装されているフローです：

1. **Authorization Request** - クライアントがユーザーを認可エンドポイントにリダイレクト
2. **User Authentication** - ユーザーがログイン
3. **User Consent** - ユーザーがスコープを承認
4. **Authorization Code** - 認可コードが発行される
5. **Token Request** - 認可コードをAccess Tokenと交換
6. **Access Token** - リソースへのアクセス権が付与される

### 将来の拡張: PKCE (Proof Key for Code Exchange)

設定で`requireProofKey(true)`に変更することで、PKCEを有効化できます：

```java
.clientSettings(ClientSettings.builder()
    .requireProofKey(true)  // PKCEを有効化
    .build())
```

### 将来の拡張: Refresh Token

現在、Refresh Token の grant type は設定済みですが、明示的なRefresh Token取得フローのUI実装は今後追加予定です。

## トラブルシューティング

### ポートが使用中の場合

`application.properties`でポート番号を変更できます：

**authorization-server/src/main/resources/application.properties:**
```properties
server.port=9000  # 別のポート番号に変更
```

**client-server/src/main/resources/application.properties:**
```properties
server.port=8081  # 別のポート番号に変更
```

### ログレベルの変更

デバッグログを無効にする場合：

```properties
logging.level.org.springframework.security=INFO
```

## 開発

### モジュール単体でのテスト実行

```bash
# Authorization Server
./gradlew :authorization-server:test

# Client Server
./gradlew :client-server:test
```

### クリーンビルド

```bash
./gradlew clean build
```

## エンドポイント一覧

### Authorization Server (http://localhost:9000)

- **GET** `/oauth2/authorize` - 認可エンドポイント
- **POST** `/oauth2/token` - トークンエンドポイント
- **GET** `/oauth2/jwks` - JWK Set エンドポイント
- **GET** `/.well-known/openid-configuration` - OpenID Connect Discovery
- **GET** `/userinfo` - UserInfo エンドポイント（OIDC）

### Client Server (http://localhost:8081)

- **GET** `/` - ホーム画面
- **GET** `/dashboard` - ダッシュボード（認証後）
- **GET** `/login/oauth2/code/{registrationId}` - OAuth2コールバック
- **GET** `/api/user` - ユーザー情報API（要認証）
- **GET** `/api/data` - データAPI（要認証）

## セキュリティに関する注意

⚠️ **このラボは検証・学習目的です。本番環境では以下の対策が必要です：**

- パスワードエンコーディングの強化（現在: `{noop}`を使用）
- 永続的なトークンストレージ（現在: In-Memory）
- HTTPS の使用
- セキュアなクライアントシークレット管理
- 本番環境用の鍵管理（現在: 起動時に動的生成）

## ライセンス

MIT License

## 参考資料

- [Spring Authorization Server Documentation](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/)
- [OAuth 2.0 RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749)
- [OpenID Connect Core 1.0](https://openid.net/specs/openid-connect-core-1_0.html)
- [PKCE RFC 7636](https://datatracker.ietf.org/doc/html/rfc7636)
