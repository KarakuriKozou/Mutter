# Mutter
プッシュ配信型データ通信を実現するつぶやき通信パッケージ
## 概要
 つぶやき通信では受信する側が送信元をフォローするという形で接続を行います。
送信元や受信先が実際には居なくともエラーにはならないように設計しています。

送信元が居なければ単にメッセージが送られないだけです。
受信先が居なければメッセージキューのような蓄積は行わずに、メッセージを即時廃棄します。

主な用途としては、動的に組み込まれるプラグイン間の連携などを想定しています。
## 基本機能

つぶやき通信は、同じ「部屋(util.mutter.Room)」を通信範囲とします。
部屋は複数設置可能でそれぞれ識別名によるシングルトンで、Room.getRoom(部屋名) と呼ぶことで取得（もしくは新規作成）できます。
この「部屋」には「座席(util.mutter.Seat)」があり、シングルトンとして 「部屋」.getSeat(座席名) と取得できます。

「部屋」と「座席」があれば、次は座る人「利用者(util.mutter.User)」が登場します。
APIを利用する場合は、継承により「利用者(util.mutter.User)」をカスタマイズします。
具体的には、実際に独り言などを聞いた場合の処理 (利用者.received(メッセージ)) が abstract なので実装する必要があります。
次にこの「利用者」は「座席」に座る (「座席」.sitDown(利用者)) ことが出来ます。
座席に座っている利用者は、当然に聞き耳を立てるだけではなく、独り言を言う([利用者].mutter(メッセージ))ことも出来ます。
また全ての座席からのつぶやきを聞いていたら処理能力がいくらあっても足りません。
「利用者」は別の座席に座っている利用者の独り言に聞き耳立てる(「利用者」.follow(座席名))必要があります。
聞き耳を立て（フォローす）れば、あとは自動的に相手のつぶやきは (利用者.received(メッセージ)) で利用者の耳に届くことになります。

ここでの要点は、独り言を言うのも聞き耳立てるのも、必ずしも相手の「利用者」を必要としないことです。
あくまで「座席」に対しての行動なのです。
一人きりだとしても、ブツブツと独り言を言い続けたり、誰もいない「部屋」でジッと聞き耳を立て続けているだけです。
害はありません。通報したりしないでください。侘びしい光景ですが。
## 拡張機能

「部屋」の中で独り言を延々とつぶやくのも趣がありますが、別の「部屋」でのつぶやきを聞きたい事もあるかと思います。
そんな需要に答えるのが「廊下(util.mutter.PassageWay)」です。
「廊下」は「部屋」と「部屋」を結んで、つぶやきを届けます。
適切に「廊下」が用意してあるならば、「利用者」はあたかも同じ「部屋」にいるように独り言や聞き耳を行うだけです。
「廊下」は単純に実装したならば「座席」で生成された全てのメッセージを拾って別の「部屋」に流します。

「廊下」は公式の設備ですが、もっと非公式な設備もあります。
それが「盗聴器(util.mutter.Microphone)」です。
端的に言えば、「部屋」の中のあらゆるメッセージをピックアップしつづけます。
「廊下」も「盗聴器」も「部屋」に取り付ける点は同じですが「盗聴器」はより低レベルな機能です。
妥当性などが判断される「以前」に、全てのメッセージが「盗聴器」には渡されます。
つまり、通常の生成ルートに先回りして「座席」と「利用者」を誂える、などのメタ処理が可能という事です。

「廊下」に流されるメッセージとしてはフォロー関連の操作のメッセージも含まれます。
参考実装されている「廊下」では、フォロー関係の操作は別の「部屋」にも自動的に適用され、つぶやきが送られます。
この機能が意図する目的はネットワークを挟んだ二個の「部屋」をミラーリングして同一の部屋として通信することです。
パフォーマンスの軽減を考慮する場合には「廊下」で不要なメッセージはフィルタするなどしてください。
ただし、送られてきたメッセージは他の「部屋」に転送はしません。
他の「部屋」に送るのは自分の「部屋」で生成されたメッセージのみです。
多対多の転送を実現したい場合には、単純に多対多に接続する「廊下」の開発を検討してください。
もしくは「盗聴器」などを利用してハブとして機能する「部屋」を作成し、それを介して転送する方式も考えられます。
