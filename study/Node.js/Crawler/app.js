let express = require('express');
let app = express();
let cors = require('cors')();
let bodyParser = require('body-parser');
let cookieParser = require('cookie-parser');
let http = require('http');
let session = require('express-session');
// let router = require('./router/main')(app);

app.set('port', 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(__dirname + '/public'));
app.use(cors);
app.use(session({
    secret: '!!crawlerProject!!',
    resave: false,
    saveUninitialized: true,
    cookie: { secure: true },
    maxAge: 1000 * 60 * 60
  }));

__rootPath = __dirname;

// routes Setting
/*************************************************************/
app.get('/', function(req, res){
    res.redirect('/html/index.html');
})
app.use('/api/socket/', require('./routes/api/socket'));
app.use('/api/login', require('./routes/api/login'));
app.use('/api/common', require('./routes/api/common'));

// error handler
/*************************************************************/
app.use(function(req, res, next){
    let err = new Error('Not Found');
    err.status = 404;
    next(err);
});

app.use(function(err, req, res, next){
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};
    
    // render the error page
    res.status(err.status || 500);
    res.render('error');
});

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express listening on port ' + app.get('port'));
});
/*************************************************************/

// socket.io
/*************************************************************/
_clients = [];
_io = require('socket.io').listen(3001);

require('events').EventEmitter.prototype.setMaxListeners(1000);

let connection = _io.on('connection', function(socket){
    _clients.push(socket);

    socket.on('disconnect', function(){
        let currentPos = _clients.indexOf(socket);

        if(currentPos != -1){
            _clients.splice(currentPos, 1);
        }
    });
})