$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题、内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求，发布帖子
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data){
			// 请求返回的数据 字符串转换为 JSON 对象
			data = $.parseJSON(data);
			// 提示框显示消息，设置显示提示框
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			// 2秒后自动隐藏提示框，若发布成共刷新，当前页面
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if (data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	);

}