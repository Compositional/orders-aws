const process = require('process')
const hooks = require('hooks');
const AWS = require('aws-sdk');

console.log("Updating AWS config to use endpoint: " + process.env.AWS_DYNAMODB_ENDPOINT)
AWS.config.update({
    region: "us-west-2",
    endpoint: process.env.AWS_DYNAMODB_ENDPOINT,
    accessKeyId: "foo",
    secretAccessKey: "bar"
});

var dynamodb = new AWS.DynamoDB();

function waitTables(timeout, done) {
  dynamodb.describeTable({TableName : "orders"},
    function(err,data) {
      if (err) {
        if (timeout > 0) {
          console.info("Retrying table query in 1s...")
          setTimeout(function() {
            waitTables(timeout - 1, done)
          }, 100)
        } else {
          console.error("Unable to query table. Error JSON: ", JSON.stringify(err, null, 2));
        }
      } else {
        console.info("Queried table: " + data)
      }
      done(err)
    }
  )
}

function createTables(done) {
  var params = {
    TableName : "orders",
    KeySchema: [
        { AttributeName: "id", KeyType: "HASH"},  //Partition key
        { AttributeName: "customerId", KeyType: "RANGE" }  //Sort key
    ],
    AttributeDefinitions: [
        { AttributeName: "id", AttributeType: "S" },
        { AttributeName: "customerId", AttributeType: "S" }
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 10,
        WriteCapacityUnits: 10
    }
  }

  dynamodb.createTable(params, function(err, data) {
    if (err) {
        console.error("Unable to create table. Error JSON:", JSON.stringify(err, null, 2));
    } else {
        console.log("Created table. Table description JSON:", JSON.stringify(data, null, 2));
    }
    done(err)
  });
}

function ObjectID(s) {
    return "e0da6ec9-601b-46bf-bc01-a63fbdc3" + (0x10000 + s).toString(16).substr(1);
}

hooks.beforeAll((transactions, done) => {
	createTables(function () {waitTables(30, done)});
});

hooks.before("/orders > POST", function(transaction, done) {
    transaction.request.headers['Content-Type'] = 'application/json';
    transaction.request.body = JSON.stringify(
	{
	    "customer":"http://users-orders-mock:80/customers/57a98d98e4b00679b4a830af",
	    "address": "http://users-orders-mock:80/addresses/57a98d98e4b00679b4a830ad",
	    "card" : "http://users-orders-mock:80/cards/57a98d98e4b00679b4a830ae",
	    "items": "http://users-orders-mock:80/carts/579f21ae98684924944651bf/items"
	}
    );

    done()

});

//const {MongoClient} = require('dynamodb');
// const ObjectID = require('dynamodb').ObjectID;
/*
let db;

const address = [
    {"_id":ObjectID(1),"_class":"works.weave.socks.users.entities.Address","number":"69","street":"Wilson Street","city":"Hartlepool","postcode":"TS26 8JU","country":"United Kingdom"},
    {"_id":ObjectID("579f21ae98684924944651c0"),"_class":"works.weave.socks.users.entities.Address","number":"122","street":"Radstone WayNet","city":"Northampton","postcode":"NN2 8NT","country":"United Kingdom"},
    {"_id":ObjectID("579f21ae98684924944651c3"),"_class":"works.weave.socks.users.entities.Address","number":"3","street":"Radstone Way","city":"Northampton","postcode":"NN2 8NT","country":"United Kingdom"}
];


const card = [
    {"_id":ObjectID("579f21ae98684924944651be"),"_class":"works.weave.socks.users.entities.Card","longNum":"8575776807334952","expires":"08/19","ccv":"014"},
    {"_id":ObjectID("579f21ae98684924944651c1"),"_class":"works.weave.socks.users.entities.Card","longNum":"8918468841895184","expires":"08/19","ccv":"597"},
    {"_id":ObjectID("579f21ae98684924944651c4"),"_class":"works.weave.socks.users.entities.Card","longNum":"6426429851404909","expires":"08/19","ccv":"381"}
];

const cart = [
    {"_id":ObjectID("579f21de98689ebf2bf1cd2f"),"_class":"works.weave.socks.cart.entities.Cart","customerId":"579f21ae98684924944651bf","items":[{"$ref":"item","$id":ObjectID("579f227698689ebf2bf1cd31")},{"$ref":"item","$id":ObjectID("579f22ac98689ebf2bf1cd32")}]},
    {"_id":ObjectID("579f21e298689ebf2bf1cd30"),"_class":"works.weave.socks.cart.entities.Cart","customerId":"579f21ae98684924944651bfaa","items":[]}
];


const item = [
    {"_id":ObjectID("579f227698689ebf2bf1cd31"),"_class":"works.weave.socks.cart.entities.Item","itemId":"819e1fbf-8b7e-4f6d-811f-693534916a8b","quantity":20,"unitPrice":99.0}
];


const customer = [
    {"_id":"579f21ae98684924944651bf","_class":"works.weave.socks.users.entities.Customer","firstName":"Eve","lastName":"Berger","username":"Eve_Berger","addresses":[{"$ref":"address","$id":ObjectID(1)}],"cards":[{"$ref":"card","$id":ObjectID("579f21ae98684924944651be")}]
    },
    {"_id":"579f21ae98684924944651c2","_class":"works.weave.socks.users.entities.Customer","firstName":"User","lastName":"Name","username":"user","addresses":[{"$ref":"address","$id":ObjectID("579f21ae98684924944651c0")}],"cards":[{"$ref":"card","$id":ObjectID("579f21ae98684924944651c1")}]},
    {"_id":"579f21ae98684924944651c5","_class":"works.weave.socks.users.entities.Customer","firstName":"User1","lastName":"Name1","username":"user1","addresses":[{"$ref":"address","$id":ObjectID("579f21ae98684924944651c3")}],"cards":[{"$ref":"card","$id":ObjectID("579f21ae98684924944651c4")}]}
];


// Setup database connection before Dredd starts testing
hooks.beforeAll((transactions, done) => {
    //var MongoEndpoint = process.env.MONGO_ENDPOINT ||  'mongodb://localhost:32769/data';
    //MongoClient.connect(MongoEndpoint, function(err, conn) {
	//if (err) {
	 //   console.error(err);
	//}

	console.log("Contacting DynamoDB")
	ddb.listTables({}, function(err, res) {
	  if (err) {
	    console.error(err);
	  } else {
	    console.log("DynamoDB tables: " + res);
	  }
	  done(err);
    });
});

// Close database connection after Dredd finishes testing
hooks.afterAll((transactions, done) => {
    db.dropDatabase();
    done();

});

hooks.beforeEach((transaction, done) => {
    db.dropDatabase(function(s, r) {
        var promisesToKeep = [
	    db.collection('customer').insertMany(customer),
	    db.collection('card').insertMany(card),
	    db.collection('cart').insertMany(cart),
	    db.collection('address').insertMany(address),
	    db.collection('item').insertMany(item)
        ];
        Promise.all(promisesToKeep).then(function(vls) {
	    done();
        }, function(vls) {
	    console.error(vls);
	    done();
        });
    })

});


hooks.before("/orders > POST", function(transaction, done) {
    transaction.request.headers['Content-Type'] = 'application/json';
    transaction.request.body = JSON.stringify(
	{
	    "customer":"http://users-orders-mock:80/customers/57a98d98e4b00679b4a830af",
	    "address": "http://users-orders-mock:80/addresses/57a98d98e4b00679b4a830ad",
	    "card" : "http://users-orders-mock:80/cards/57a98d98e4b00679b4a830ae",
	    "items": "http://users-orders-mock:80/carts/579f21ae98684924944651bf/items"
	}
    );

    done()

});

hooks.before("/orders > GET", function(transaction, done) {
    transaction.request.headers["User-Agent"] = "curl/7.43.0";
    transaction.request.headers["Accept"] = "*\/*";
    done();
})
*/