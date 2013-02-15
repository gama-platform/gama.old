library(sp)
library(maptools)
library(lattice)
#library(gstat)
lighttrap <- read.csv(file="D:/PSN-Simulation/RCaller/Data/Three_Provinces_Lighttraps.csv",head=TRUE,sep=",")
region_grid <- read.csv(file="D:/PSN-Simulation/RCaller/Data/Three_Provinces_Grids_6060.csv",head=TRUE,sep=",")
rg <- readShapeSpatial("D:/PSN-Simulation/RCaller/Data/3_Provinces_UTM.shp")
rg1<-as(rg,"SpatialPolygons")
coordinates(lighttrap)=~x+y
pts = region_grid[c("x", "y")]
predictiongrid = SpatialPixels(SpatialPoints(pts))
gridded(predictiongrid) = TRUE
v_uk = gstat::variogram(Day19~x+y, lighttrap)
uk_model = gstat::fit.variogram(v_uk, gstat::vgm(1, "Exp", 40000, 0)) 
zn_uk = gstat::krige(Day19~x+y, lighttrap, predictiongrid, model = uk_model)
zn = zn_uk
zn[["mekong"]] <- zn_uk[["var1.pred"]]
zn[["se_mekong"]] = sqrt(zn_uk[["var1.var"]])
text1 = list("sp.text", c(1205000, 530000), "0", cex = .5, which = 4)
write.csv(zn[["se_mekong"]],"D:\\PSN-Simulation\\RCaller\\RGama\\stdDeviation.csv")
result<-zn[["se_mekong"]]