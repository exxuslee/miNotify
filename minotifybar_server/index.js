let admin = require("firebase-admin");
let serviceAccount = require("./micokepushbar-firebase-adminsdk-immha-6d3287b0ef.json");
const sqlite3 = require('sqlite3').verbose();
const fs = require('fs');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://micokepushbar.firebaseio.com"
});

// const ref = fdb.ref();
// const fdb = admin.database();

const fistCycle = 60;

let ikv01 = new Array(180);
let ikv02 = new Array(180);
let ikv03 = new Array(180);
let ikv04 = new Array(180);
let ikv05 = new Array(180);
let ikv06 = new Array(180);
let ikv07 = new Array(180);
let ikv08 = new Array(180);
let ikv09 = new Array(180);
let ikv10 = new Array(180);
let ikv11 = new Array(180);
let ikv12 = new Array(180);
let ikv13 = new Array(180);
let  count01 = 0;
let  count02 = 0;
let  count03 = 0;
let  count04 = 0;
let  count05 = 0;
let  count06 = 0;
let  count07 = 0;
let  count08 = 0;
let  count09 = 0;
let  count10 = 0;
let  count11 = 0;
let  count12 = 0;
let  count13 = 0;



function message(num_bake, Imax, array, ikv){
    let topic;
    let title;

    let myJsonArray = JSON.stringify(array);

    return new Promise(resolve => {
        console.log(num_bake + '  ' + Imax);
        if (num_bake > 899 && Imax >= 600) topic = "kc4m";
        else if (num_bake > 899 && Imax >= 340) topic = "kc4b";
        else if (num_bake > 699 && Imax >= 600) topic = "kc3m";
        else if (num_bake > 699 && Imax >= 340) topic = "kc3b";
        else if (num_bake > 499 && Imax >= 500) topic = "kc2m";
        else if (num_bake > 499 && Imax >= 240) topic = "kc2b";
        else if (num_bake > 99 && Imax >= 600) topic = "kc1m";
        else if (num_bake > 99 && Imax >= 240) topic = "kc1b";
        else topic = "kc0b";


        if (Imax > 500) title = "Максимальный ток";
        else title = "Повышенный ампераж";

        let message = {
            topic: topic,
            data: {
                title: "Печь №"+num_bake+", КВ-"+ikv+", ток "+Imax,
                body: title,
                myJsonArray: myJsonArray
            }
        };

        admin.messaging().send(message)
            .then((response) => {
                // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
        resolve();
    });
}

function sqlite_read(i, Imax, array) {
    return new Promise(resolve => {
        console.log(sqlite_read);
        let db = new sqlite3.Database("C:/SQLite/DB/"+i+"/db.sqlite", (err) => {
            if (err) {
                console.error(err.message);
            }
        });

        db.serialize(() => {
            db.each(`SELECT id FROM bake LIMIT 1`, (err, row) => {
                if (err) {
                    console.error(err.message);
                }
                else {
                    message(row.id, Imax, array, i)
                }
            });
        });

        db.close((err) => {
            if (err) {
                console.error(err.message);
            }
        });

        resolve();
    });
}

async function check_current(array, num_kv) {
    let Imax = 0;
    for (let i = 40; i < 100; ++i) {
        if (array[i] > Imax) Imax = array[i];
    }


    if (Imax >= 240 && num_kv === 1) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 2) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 3) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 4) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 5) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 6) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 7) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 240 && num_kv === 8) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 340 && num_kv === 9) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 340 && num_kv === 10) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 340 && num_kv === 11) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 340 && num_kv === 12) await sqlite_read(num_kv, Imax, array);
    if (Imax >= 340 && num_kv === 13) await sqlite_read(num_kv, Imax, array);
}

async function read_file() {
    (async () => {
        for (let i = 1; i < 14; ++i) {
            await data_read(i);
        }
    })();


    function data_read(i) {
        fs.readFile("C:/SQLite/DB/" + i + "/Ishtangi.txt", 'utf8', function (err, data) {
            if (err) {
                return console.log(err);
            }
            switch(i) {
                case 1:
                    count01++;
                    ikv01.shift();
                    ikv01.push(parseInt(data));
                    if (count01 > fistCycle) {
                        check_current(ikv01, 1);
                        count01=0;
                    }

                    break;
                case 2:
                    count02++;
                    ikv02.shift();
                    ikv02.push(parseInt(data));
                    if (count02 > fistCycle) {
                        check_current(ikv02, 2);
                        count02=0;
                    }
                    break;
                case 3:
                    count03++;
                    ikv03.shift();
                    ikv03.push(parseInt(data));
                    if (count03 > fistCycle) {
                        check_current(ikv03, 3);
                        count03=0;
                    }
                    break;
                case 4:
                    count04++;
                    ikv04.shift();
                    ikv04.push(parseInt(data));
                    if (count04 > fistCycle) {
                        check_current(ikv04, 4);
                        count04=0;
                    }
                    break;
                case 5:
                    count05++;
                    ikv05.shift();
                    ikv05.push(parseInt(data));
                    if (count05 > fistCycle) {
                        check_current(ikv05, 5);
                        count05=0;
                    }
                    break;
                case 6:
                    count06++;
                    ikv06.shift();
                    ikv06.push(parseInt(data));
                    if (count06 > fistCycle) {
                        check_current(ikv06, 6);
                        count06=0;
                    }
                    break;
                case 7:
                    count07++;
                    ikv07.shift();
                    ikv07.push(parseInt(data));
                    if (count07 > fistCycle) {
                        check_current(ikv07, 7);
                        count07=0;
                    }
                    break;
                case 8:
                    count08++;
                    ikv08.shift();
                    ikv08.push(parseInt(data));
                    if (count08 > fistCycle) {
                        check_current(ikv08, 8);
                        count08=0;
                    }
                    break;
                case 9:
                    count09++;
                    ikv09.shift();
                    ikv09.push(parseInt(data));
                    if (count09 > fistCycle) {
                        check_current(ikv09, 9);
                        count09=0;
                    }
                    break;
                case 10:
                    count10++;
                    ikv10.shift();
                    ikv10.push(parseInt(data));
                    if (count10 > fistCycle) {
                        check_current(ikv10, 10);
                        count10=0;
                    }
                    break;
                case 11:
                    count11++;
                    ikv11.shift();
                    ikv11.push(parseInt(data));
                    if (count11 > fistCycle) {
                        check_current(ikv11, 11);
                        count11=0;
                    }
                    break;
                case 12:
                    count12++;
                    ikv12.shift();
                    ikv12.push(parseInt(data));
                    if (count12 > fistCycle) {
                        check_current(ikv12, 12);
                        count12=0;
                    }
                    break;
                case 13:
                    count13++;
                    ikv13.shift();
                    ikv13.push(parseInt(data));
                    if (count13 >| fistCycle) {
                        check_current(ikv13, 13);
                        count13=0;
                    }
                    break;
                default: console.log("KV missing..");
            }
        })
    }
}

setInterval(read_file,     1000);

// function read_firebase(){
//     ref.once("value").then(function (snapshot1) {
//         snapshot1.forEach(function (snapshot2) {
//             console.log(snapshot2.key);
//             snapshot2.forEach(function (snapshot3) {
//                 key0 = snapshot3.key;
//                 key1 = snapshot3.val();
//                 console.log(key0+": "+key1);
//             })
//         })
//     })
// }
