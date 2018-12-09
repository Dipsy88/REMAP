import csv

#Location of input and output
fileIn = "iofrol.csv"
fileOut = "iofrolOut_oneMissing-1.arff"
skipFrom = 1

cycle_set = set()  # set of test cycles executed
testCase_map = {}  # dictionary of test with its execution result for different test cycle
cycle_map = {}      # dictionary of test cycles with a list of test cases executed
skip_cases=[]
cycle_consider = 1


def readFile():
    ''' read the input file
    stores in hashmaps the values for different test cases'''
    isFirstLine = True
    with open(fileIn, 'r') as csvFile:
        reader = csv.reader(csvFile, delimiter =';')
        # Input csv file has this particular format
        for col in reader:
            if isFirstLine:
                isFirstLine = False
                continue
            testcase_id = col[1]
            execution_time=int(col[2])
            result = (int)(col[5])
            cycle = (int)(col[6])
            cycle_set.add(cycle)    # add all the used test cycle to a set
            if result == 1:
                status = "fail"
            else:
                status = "pass"

            # add test case in test cycle
            testCase_set = set()
            if cycle in cycle_map:
                testCase_set = cycle_map[cycle]
            testCase_set.add(testcase_id)
            cycle_map[cycle] = testCase_set

            # add test case with latest result in test case map
            testCycleResult = {}
            if testcase_id in testCase_map:
                testCycleResult = testCase_map[testcase_id]
            testCycleResult[cycle] = result
            testCase_map[testcase_id] = testCycleResult


def calculateOutput(skipFrom):
    ''' Input row number from which data is not going to be written
        Converts a matrix form of output and returns it'''
    # output file
    global cycle_consider
    f = open(fileOut,'w')
    numRow = len(cycle_set) - skipFrom + 1 # last row is still written as missing data
    numColumn = len(testCase_map)
    Matrix = [[0 for x in range(numColumn)] for y in range (numRow)]
    print("@RELATION name\n", file=f)      # name of the file, name here can be change to name of dataset
    column = 0
    # go through each test case and put value for the results
    for testCase, values in testCase_map.items():
        print("@ATTRIBUTE %s" %testCase, " {pass,fail}", file=f)
        row = 0
        for cycle in cycle_set:     # go through all the test cycles
            if (row == numRow-1):
                Matrix[row][column] = "?"
                cycle_consider = cycle
                if cycle not in values:
                    print (testCase)
            else:
                if cycle in values:   # if test case was executed in the particular test cycle
                    if (values[cycle] == 1):
                        value = "fail"
                    else:
                        value = "pass"
                    Matrix[row][column] = value
                else:
                    #print(str(row)+" " + str(column))
                    Matrix[row][column] = "?"
            row += 1
            if (row >= numRow):
                break

        column += 1
    print("", file=f)
    print("@data \n", file=f)
    f.close()
    return Matrix



# need to do this now
def writeTestCase():
    ''' read the input file
        stores in hashmaps the values for different test cases'''
    global cycle_consider
    tcMap = {}
    isFirstLine = True
    with open(fileIn, 'r') as csvFile:
        reader = csv.reader(csvFile, delimiter=';')
        # Input csv file has this particular format
        for col in reader:
            if isFirstLine:
                isFirstLine = False
                continue
            testcase_id = col[1]
            execution_time = int(col[2])
            result = (int)(col[5])
            cycle = (int)(col[6])
            resultTemp = True
            if result == 1:
                status = "fail"
                resultTemp = False
            else:
                status = "pass"

            datamodel_class = TC()
            if testcase_id in tcMap:
                datamodel_class = tcMap[testcase_id]
            if cycle < cycle_consider:
                datamodel_class.setParams(int(execution_time), resultTemp)
                tcMap[testcase_id] = datamodel_class

    # loop hashmap now
    for key,value in tcMap.items():
        tc = value
        time = float(tc.totalExecutionTime/tc.totalExecuted)
        fdc = float(tc.totalFailed/tc.totalExecuted)
        print(str(key) + "\t" + str(time) + "\t" + str(fdc))


class TC(object):
    def __init__(self):
        self.failed = 0
        self.total = 0
        self.totalExecutionTime = 0
        self.totalFailed = 0
        self.totalExecuted = 0


    def setParams(self,totalExecutionTime,failed):
        self.totalExecutionTime += totalExecutionTime
        if not failed:
            self.totalFailed += 1
        self.totalExecuted += 1



def writeOutput(matrix):
    ''' Takes in matrix and writes the test case execution results in the file '''
    f = open(fileOut, 'a+')
    for row in matrix:
        print((','.join(map(str, row))), file=f)
    f.close()


readFile()
cycle_set = sorted(cycle_set)       # sorts the test cycle
matrix = calculateOutput(skipFrom)
writeOutput(matrix)
writeTestCase()