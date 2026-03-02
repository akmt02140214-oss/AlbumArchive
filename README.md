# Album Archive
ホーム画面

<img width="1535" height="893" alt="image" src="https://github.com/user-attachments/assets/a3c19505-ec95-4b48-8e09-2f55d9e91b63" />


## 概要
Album Archiveは、これまでに聞いた音楽アルバムを効率的に記録・管理するためのWebアプリケーションです。
サブスクリプション音楽サービスのライブラリ機能では、保存数の増加に伴い、アプリ内処理が重くなり、過去に聞いたアルバムを振り返ることが難しくなるという課題を感じました。

そこで、必要な情報だけを整理し、管理できるアプリを開発しようと思いました。

## 動作デモ
アルバム登録処理

![Image](https://github.com/user-attachments/assets/01782765-997a-47cd-a8bb-1b05bce5f85a)

アルバム編集・削除処理

![Image](https://github.com/user-attachments/assets/eb98f1a1-8469-4d5a-ba38-87c2b38bdf2e)

## 解決したい課題
・過去に聴いたアルバムを忘れてしまう
・サブスクリプション音楽サービスで管理すると、動作が重くなることがある
・自分の聴いてきた音楽の履歴を振り返る

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

## 現在の実装機能
- アルバム検索機能
- アルバム追加機能
- アルバム編集機能
- アルバム削除機能
- 登録済みアルバム並び替え機能
- アーティスト・ジャンルランキング表示機能

## 今後実装予定
- ログイン機能
- 登録済みアルバム検索機能

## 設計思想
本アプリでは、情報を増やすのではなく整理することを重視しています。
機能を過剰に増やすのではなく、必要最小限の構成で管理できる設計を目指しています。


## 今後の展望
現在は基本機能の実装段階ですが、今後は統計データや検索制度の向上など、整理することを第一に考えて機能を追加していく予定です。
また、現在は手を動かしながら実装しているため、テストコードは後々実装していく予定です。
