var express = require('express')
,http = require('http')
,fs = require('fs');

var app = express();


var bodyParser = require('body-parser');


app.set('view engine','jade');
app.set('views','views');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended : false}));   //body-paser middle-wear 를 먼저 실행 후 라우팅
app.use(express.static('public'));

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var docs1;

mongoose.connect('mongodb://localhost:27017/jp')


var db = mongoose.connection;
db.on('error',console.error.bind(console,'connection error'));
db.once('open',function(){
    console.log('db connect');
});



var user_schema = new Schema({
    id : String,
    pw : String
});

var userModel = mongoose.model('userModel',user_schema,'user');


function addUser(id,pw){
    var newUser = new userModel;

    newUser.id = id;
    newUser.pw = pw;

    console.log('add user : ' + newUser.id + newUser.pw);

    newUser.save(function (err) {
        if (err) throw err;
    });
}


app.get('/',function(req,res){
    res.render('index');
});

app.get('/register',function(req,res){
    res.render('register');
});
//
app.get('/download',function(req,res){
    var filename = 'client_final.zip';
    var file = fs.createReadStream(filename, {flags: 'r'} );
 
    file.pipe(res);

 
});
//

app.post('/register_process',function(req,res){
    var id = req.body.id;
    var pw = req.body.pw;
    console.log(id + '+' + pw);
    addUser(id,pw);
    if(id !=0){
      res.render('register_success');
    }
})
app.post('/register_success',function(req,res){
    res.render('register_success');
})



app.listen('3000',function(){
  console.log('express web server start');
})