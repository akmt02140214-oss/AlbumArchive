# Album Archive
ホーム画面

<img width="1535" height="893" alt="image" src="https://github.com/user-attachments/assets/a3c19505-ec95-4b48-8e09-2f55d9e91b63" />


## 概要
Album Archiveは、これまでに聴いた音楽アルバムを効率的に記録・管理するためのWebアプリケーションです。
サブスクリプション音楽サービスのライブラリ機能では、保存数の増加に伴い、アプリ内処理が重くなり、過去に聞いたアルバムを振り返ることが難しくなるという課題を感じました。この課題に対して、必要な情報だけを整理して管理できる仕組みを目指して開発しています。

## 背景と課題
- 過去に聴いたアルバムを忘れてしまう
- サブスクリプション音楽サービスで管理すると、動作が重くなることがある
- 自分に必要な項目だけでシンプルに管理したい

## 動作デモ
アルバム登録処理

![Image](https://github.com/user-attachments/assets/01782765-997a-47cd-a8bb-1b05bce5f85a)

アルバム編集・削除処理

![Image](https://github.com/user-attachments/assets/eb98f1a1-8469-4d5a-ba38-87c2b38bdf2e)

## 現在の実装機能

- アルバム検索機能
- アルバム追加機能
- アルバム編集機能
- アルバム削除機能
- 登録済みアルバム並び替え機能
- アーティスト・ジャンルランキング表示機能

## 使用技術
- Java 17
- Spring Boot 4.0.2
- HTML / CSS
- Bootstrap 5.0.2
- Thymeleaf
- MySQL
- Lombok
- Maven
- Docker/Docker Compose
- 外部API（Last.fm API）

- ## データベース設計

| albums | album_genres |
|--------|--------------|
| id (PK) | id (PK) |
| album_name | album_id (FK) |
| artist_name | genre |
| image_url | |
| rating | |
| memo | |
| register_date | |

## テスト
- Controller, Service, Repositoryテスト
- DTOテスト

今後は異常系テストを追加する予定です。

## 苦労した点と工夫

- 外部APIを使用したDB登録などの動画教材で学んだことがなかったもの
- 機能を複雑にせずに最小限の構成にする

## 今後実装予定
- 登録済みアルバム検索機能
- ログイン機能
- デプロイ（AWS)

## 起動例

- 1, DockerでMYSQLを起動
- 2, アプリを起動
- 3, ブラウザでアクセス

## 今後の展望
現在は基本機能の実装段階ですが、今後は統計データや検索制度の向上など、整理することを第一に考えて機能を追加していく予定です。
