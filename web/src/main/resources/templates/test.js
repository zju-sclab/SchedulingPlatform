

function addElementLi(obj,attachArr) {
    var ul = document.getElementById(obj);
    
    for (i = 0; i < attachArr.length; i++) { 
        //添加 li
        var li = document.createElement("li");

        //设置 li 属性，如 id
        li.setAttribute("id", "newli");

        li.innerHTML = attachArr[i];
        li.onclick = function(){
            chooseRemote(attachArr[i]);
            console.log("clicked!");
        }
        li.style.borderBottom = "1px solid red";
        ul.appendChild(li);
    }
    
}


var vm = new Vue({
    el:'#app',
    data(){
        return {
            list:[]
        }
    },   
    methods:{
        getInfo(){
           this.$http.get('http://localhost:8080/api/v2/info/car/connected',{params : {page:1,size:2}}).then(result=> {

                console.log(result.body.attach);
               att = ["123","356"];
            //    addElementLi("carVin",result.body.attach);
                this.list = result.body.attach;
           })  
        },
        //get请求，通过function执行的成功的回调函数，得到body和data等数据    

        postInfo(){
            
        },
        //post请求，中间花括号空的部分为提交给服务器的数据这里默认，emulateJSON:true,将手动提交表单格式设置为application/x-www-form-urlencoded格式

        jsonpInfo(){
            this.$http.jsonp('http://jsonplaceholder.typicode.com/users').then(result=>{
                console.log(result.body);
            })
        },

        chooseRemote(carVin){
            this.$http.post('http://localhost:8080/api/v2/info/car/choose/remote',{vin:carVin},{emulateJSON:true}).then(function(result){
                console.log(result.body);    
            })
        }
    } 
 });
