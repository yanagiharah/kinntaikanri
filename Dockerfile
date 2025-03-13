# Java 21 を使用する
FROM eclipse-temurin:21-jdk AS build

# 作業ディレクトリを設定
WORKDIR /app

# プロジェクトの全ファイルをコンテナにコピー
COPY . /app/.

# Java のバージョンをログに出力（確認用）
RUN java -version

# Maven を実行してビルド（キャッシュを有効化）
RUN --mount=type=cache,id=cache-key/m2,target=/root/.m2 chmod +x ./mvnw && ./mvnw -B -DskipTests clean install

# 実行する JAR ファイルのパスを指定
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]