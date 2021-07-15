const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.updateQuestionNumber = functions.https.onRequest(
    (request, response) => {
        admin
            .firestore()
            .collection("mcq")
            .get()
            .then((res) => {
                const allMcqs = [];
                res.forEach((datax) => {
                    allMcqs.push({
                        id: datax.id,
                    });
                });
                console.log(allMcqs.length);

                let batch = admin.firestore().batch();
                for (let x = 0; x < allMcqs.length; x++) {
                    batch.update(
                        admin.firestore().collection("mcq").doc(allMcqs[x].id),
                        {
                            questionNumber: x,
                        }
                    );
                    if (x % 500 == 0) {
                        batch.commit();
                        batch = admin.firestore().batch();
                    }
                }
                batch.commit();
                return response.send("OK");
            })
            .catch((err) => {
                return response.send("ERROR " + err);
            });
    }
);

exports.createNewMockTest = functions.https.onRequest((request, response) => {
    admin
        .firestore()
        .collection("mcq")
        .orderBy("questionNumber", "desc")
        .limit(1)
        .get()
        .then((res) => {
            let totalQuestions = 0;
            res.forEach((data) => {
                totalQuestions = data.data().questionNumber;
            });
            console.log("Total Question found = " + totalQuestions);
            const mockQuestionList = [];
            while (mockQuestionList.length != 30) {
                const number = Math.floor(Math.random() * totalQuestions + 1);
                if (!mockQuestionList.includes(number)) {
                    mockQuestionList.push(number);
                }
            }
            console.log("Question List = " + mockQuestionList.toString());
            return response.send(mockQuestionList.toString());
        });
});
