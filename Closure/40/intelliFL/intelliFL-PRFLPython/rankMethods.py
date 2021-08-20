import os
import sys
import numpy as np
import pandas as pd


def computeSuspScores(inputMatrix):
    # prevent "divided by 0"
    epsilon = 0.0000000001

    metricDict = dict()

    max_EF = np.amax(inputMatrix[:, 0])
    # prevent "0 divides 0"
    if max_EF == 0.0:
        pr_EF = 0.0 * inputMatrix[:, 0]
    else:
        pr_EF = inputMatrix[:, 0] / max_EF * inputMatrix[:, 4]

    max_EP = np.amax(inputMatrix[:, 1])
    # prevent "0 divides 0"
    if max_EP == 0.0:
        pr_EP = 0.0 * inputMatrix[:, 1]
    else:
        pr_EP = inputMatrix[:, 1] / max_EP * inputMatrix[:, 5]

    pr_NF = inputMatrix[:, 4] - pr_EF
    pr_NP = inputMatrix[:, 5] - pr_EP

    metricDict["PR_Tarantula"] = (pr_EF / (pr_EF + pr_NF + epsilon)) / (pr_EF / (pr_EF + pr_NF + epsilon) + pr_EP / (pr_EP + pr_NP + epsilon) + epsilon)
    metricDict["PR_SBI"] = pr_EF / (pr_EF + pr_EP + epsilon)
    metricDict["PR_Ochiai"] = pr_EF / np.sqrt((pr_EF + pr_EP) * (pr_EF + pr_NF) + epsilon)
    metricDict["PR_Jaccard"] = pr_EF / (pr_EF + pr_EP + pr_NF + epsilon)
    metricDict["PR_Ochiai2"] = (pr_EF * pr_NP) / np.sqrt((pr_EF+pr_EP) * (pr_NF+pr_NP) * (pr_EF+pr_NP) * (pr_EP+pr_NF) + epsilon)
    metricDict["PR_Kulczynski"] = pr_EF / (pr_NF + pr_EP + epsilon)
    metricDict["PR_Dstar2"] = (pr_EF * pr_EF) / (pr_NF + pr_EP + epsilon)
    metricDict["PR_Op2"] = pr_EF - pr_EP / (pr_EP + pr_NP + 1 + epsilon)

    return metricDict


def readCSV(dataPath):
    csvFile = os.path.join(dataPath, "PageRank_scores.csv")
    df = pd.read_csv(csvFile, sep=',', header=0)
    methodList = df[df.columns[0]].tolist()
    prMatrix = df[df.columns[1:]].as_matrix()
    return [methodList, prMatrix]


def saveRankingResult(outputPath, methodList, metricDict,SpecTech):
    metricNameList = [
        "PR_Tarantula", "PR_SBI", "PR_Ochiai", "PR_Jaccard",
        "PR_Ochiai2", "PR_Kulczynski", "PR_Dstar2", "PR_Op2"
    ]

    outputFolder = os.path.join(outputPath, "pr_ranking")
    if os.path.exists(outputFolder) is False:
        os.mkdir(outputFolder)

    for metric_i in metricNameList:
        if metric_i==SpecTech:
            rankingVec_i = metricDict[metric_i]
            sortedIndexList = list(np.argsort(rankingVec_i)[::-1])

            outputFile_i = os.path.join(outputFolder, (metric_i + ".csv"))
            print("===============================================================================")
            print("Some most suspicious methods and their suspiciousness values are as follows:")
            
            with open(outputFile_i, "w") as file:
                count=1
                for i in sortedIndexList:
                    if count<11:
                        print(str(methodList[i]) + " " + str(rankingVec_i[i]) + "\n")
                        count=count+1
                    file.write(str(methodList[i]) + "," + str(rankingVec_i[i]) + "\n")
            print("For the full rank list of methods, please refer to the output file in the directory:./"+outputFolder+"/"+"\n")
            print("===============================================================================")

if __name__ == "__main__":
    dataPath = sys.argv[1]
    outputPath = sys.argv[2]
    SpecTech=sys.argv[3]

    methodList, prMatrix = readCSV(dataPath)
    metricDict = computeSuspScores(prMatrix)
    saveRankingResult(outputPath, methodList, metricDict,SpecTech)

