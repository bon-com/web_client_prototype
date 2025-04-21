◆web_client_prototypeに関するメモ

◇ポイント
・非同期な処理に対応
・チェーンメソッドで直感的に実装可能
・同期的にも使用可能（Spring-MVCで使用する場合、基本的に同期的に使用することでRestTemplateと同じように扱える）

◇全体的な共通事項
・block()を使用して同期通信する　※Spring-MVCではすべてにつける

◇基本的なメソッド
以下をおさえておけば、WebClientを使ったすべてのHTTPメソッド（GET / POST / PUT / DELETE 等）に対応可能
① bodyToMono：最もシンプルなレスポンス処理（ステータスやヘッダーは気にしない）
② toEntity：ステータス・ヘッダー・ボディすべて取得（単一オブジェクト）
③ toEntityList：レスポンスボディがリスト形式（＋ステータス・ヘッダー）
④ exchangeToMono：任意のステータス処理、カスタムエラー処理、ヘッダー処理など柔軟に制御

◇その他補足
　・Bean定義について
　　WebClientをxmlベースでBean定義しようとしてもどうしてもうまくいかなかった
　　そのため、JavaベースのBean定義としている

　・リクエスト送信について
　　基本的に以下の2つ
　　　 - retrieve()：簡単・高速にレスポンスボディを扱えるが、ステータスコード制御は一応可能だがヘッダなど見れない
　　　   また、ClientResponseを受け取ることができない。
　　　 - exchangeToMono()：ClientResponseを通じて、レスポンスのすべて（ステータス・ヘッダ含む）をフル制御できる

　・exchangeToMono
　　【exchangeToMono(res -> { ... })】におけるresオブジェクトは「org.springframework.web.reactive.function.client.ClientResponse」となる。
　　　- ClientResponseについて
　　　　　　　　⇒　概要： WebClient のレスポンス全体（ステータス、ヘッダー、ボディなど）を表すオブジェクト
　　　　　　　　⇒　主なメソッド：
　　　　　　　　　　　res.statusCode() → HttpStatus を取得（ステータスコード）
　　　　　　　　　　　res.bodyToMono(Class<T>) → レスポンスボディを指定型に変換
　　　　　　　　　　　res.bodyToFlux(Class<T>) → ボディを複数要素（リスト）で取得
　　　　　　　　　　　res.headers() → ヘッダー取得
　　　　　　　　　　　res.createException() → エラー応答を例外化（Mono<WebClientresException>）
　　　- Mono.error()について
　　　　　　　　⇒　exchangeToMono使用時、エラーが発生した場合はMono.error()でカスタム例外などをスロー
　　　　　　　　⇒　例外をスローするMonoを返却する
　　　　　　　　⇒　block()が実行されたとき、Monoの中に入っている例外がスローされる

　・WebClientの例外
　　java.lang.RuntimeException
　　└── org.springframework.web.reactive.function.client.WebClientException　⇒ WebClientに関係する全ての例外の共通の親
　　    ├── WebClientRequestException　⇒ 通信エラー系（接続できない、タイムアウトなど）
　　    ├── WebClientResponseException　⇒ HTTPレスポンスエラー系（ステータスコードがエラーなど）
　　    │   ├── BadRequest (400)
　　    │   ├── Unauthorized (401)
　　    │   ├── Forbidden (403)
　　    │   ├── NotFound (404)
　　    │   ├── InternalServerError (500)
　　    │   └── ... 他、HTTPステータスごとにサブクラスがある
　　    └── UnknownHttpStatusCodeException　⇒ 未定義のステータス（218, 599など）へのレスポンス時

　・カスタム例外
  以下を定義
　　　- 4xxエラー：ClientErrorException
　　　- 5xxエラー：ServerErrorException
　　　- 想定外エラー：UnknownErrorException

◇インデックス
■事前準備：カスタム例外作成、WebClientのBean定義、API疎通クラス作成
■type01：GET通信（retrieve + bodyToMono）で、レスポンスボディをJSON文字列で取得
■type02：GET通信（retrieve + bodyToMono）で、レスポンスボディをオブジェクトで取得
■type03：GET通信（retrieve + bodyToMono）で、レスポンスボディをオブジェクトリストで取得（ParameterizedTypeReferenceを使用）
■type04：GET通信（retrieve + toEntity）で、レスポンスをResponseEntityで取得（ボディ部：オブジェクト）
■type05：GET通信（retrieve + toEntityList）で、レスポンスをResponseEntityで取得（ボディ部：リスト）
■type06：GET通信（exchangeToMono）で、レスポンスボディをJSON文字列で取得
■type07：GET通信（exchangeToMono）で、レスポンスボディをオブジェクトで取得
■type08：GET通信（exchangeToMono）で、レスポンスボディをリストで取得
■type09：GET通信（exchangeToMono）で、レスポンスをResponseEntityで取得（ボディ部：オブジェクト）
■type10：GET通信（exchangeToMono）で、レスポンスをResponseEntityで取得（ボディ部：オブジェクトリスト）
■type11：retrieveでリクエスト送信した際に例外ハンドリング


