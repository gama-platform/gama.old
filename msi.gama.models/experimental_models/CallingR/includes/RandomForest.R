# Load the package:
library(randomForest)
# Read data from iris:
data(iris)
nrow<-length(iris[,1])
ncol<-length(iris[1,])
idx<-sample(nrow,replace=FALSE)
trainrow<-round(2*nrow/3)
trainset<-iris[idx[1:trainrow],]
# Build the decision tree:
trainset<-iris[idx[1:trainrow],]
testset<-iris[idx[(trainrow+1):nrow],]
# Build the random forest of 50 decision trees:
model<-randomForest(x= trainset[,-ncol], y= trainset[,ncol], mtry=3, ntree=50)
# Predict the acceptance of test set: 
pred<-predict(model, testset[,-ncol], type="class")
# Calculate the accuracy:
acc<-sum(pred==testset[, ncol])/(nrow-trainrow)