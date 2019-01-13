import re

from sys import stdin

file_rules= "JRIP.txt"


def extract_ids(tokens):
    return list(map(lambda s: s.split("=")[0].strip().replace("(", ""),
    filter(lambda s: "failAll" in s,
    map(lambda s: s.strip(), filter(lambda s: s != " ", tokens)))))

def extract_ids_All(tokens):
    return list(map(lambda s: s.split("=")[0].strip().replace("(", ""),
    map(lambda s: s.strip(), filter(lambda s: s != " ", tokens))))

def extract_All(tokens):
    return list(map(lambda s: s.split("=")[0].strip().replace("(", ""),
    map(lambda s: s.strip(), filter(lambda s: s != " ", tokens))))


def extract_ids_all_failed(tokens):
    return list(map(lambda s: s+"f",
        map(lambda s: s.split("=")[0].strip().replace("(", ""),
    filter(lambda s: "failAll" in s,
    map(lambda s: s.strip(), filter(lambda s: s != " ", tokens))))))

def extract_ids_all_passed(tokens):
    return list(map(lambda s: s+"p",
        map(lambda s: s.split("=")[0].strip().replace("(", ""),
    filter(lambda s: "null" in s,
    map(lambda s: s.strip(), filter(lambda s: s != " ", tokens))))))



def find_all_rules_fail():
    consequent_set = set()
    with open(file_rules) as f:
        rulePrecedent = None
        for line in f:
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if "failAll" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    rulePrecedent = ",".join(antecedent_fail)
                    if (len(antecedent_fail) >0 and len(antecedent_pass) >0):
                        rulePrecedent += ","
                    rulePrecedent += ",".join(antecedent_pass)

                    if percent >= 90 and rulePrecedent !="" and total>4:
                        print(rulePrecedent + "=" + consequent+"f")
                    if percent >= 90 and rulePrecedent == "":
                        #print(consequent)
                        consequent_set.add(consequent)
    return consequent_set


def find_all_rules_pass_using_consequent(consequent_set):
    tc_dependency = {}

    with open(file_rules) as f:
        rulePrecedent = None
        startNew = True
        mapDependencies = set()
        tc_ignore_set = set()

        percentage_total = 0
        percentage_incorrect =  0
        for line in f:
            if ('rules' in line):
                startNew = True
                mapDependencies = set()
                tc_ignore_set = set()
                percentage_total = 0
                percentage_incorrect = 0
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))

                if "failAll" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    percentage_total += total
                    percentage_incorrect += incorrect

                    if percent >= 90:
                        rulePrecedent = ",".join(antecedent_fail)
                        if (len(antecedent_fail) >0 and len(antecedent_pass) >0):
                            rulePrecedent += ","
                        rulePrecedent += ",".join(antecedent_pass)

                    if (rulePrecedent != "" and percent >= 90):
                        mapDependencies.add(rulePrecedent)
                        startNew = False

                else:
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    if percentage_total>0:
                        percent_final = (percentage_total - percentage_incorrect) / percentage_total * 100
                    else:
                        percent_final = 0

                    if startNew is False and len(mapDependencies)>0:
                        if percent >= 90 and percent_final >= 90:
                            for rulePrecedent in mapDependencies :
                                #print(rulePrecedent + "=" + consequent+"f")
                                if consequent in consequent_set and consequent not in tc_ignore_set:
                                    each = list(map(lambda s: s.strip(), rulePrecedent.split(',')))
                                    for temp in each:
                                        cons = match_name(temp)
                                        #print (cons + "=" + consequent+"p")
                                        names = set()
                                        if cons in tc_dependency:
                                            names = tc_dependency[cons]
                                        names.add(consequent+'p')
                                        tc_dependency[cons] = names
        for key, value in tc_dependency.items():
            print(key + "=", end="")
            rulePrecedent = ",".join(value)

            print(rulePrecedent)


def find_all_rules_pass_using_consequent_allHigher(consequent_set):
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        startNew = True
        mapDependencies = set()
        tc_ignore_set = set()
        percentage_total = 0
        percentage_incorrect = 0
        for line in f:
            if ('rules' in line):
                startNew = True
                mapDependencies = set()
                tc_ignore_set = set()
                percentage_total = 0
                percentage_incorrect = 0
                mapDependenciesPercentTrue = {}
                mapDependenciesPercentFalse = {}
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))

                if "failAll" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    percentage_total += total
                    percentage_incorrect += incorrect

                    rulePrecedent = ",".join(antecedent_fail)
                    if (len(antecedent_fail) > 0 and len(antecedent_pass) > 0):
                        rulePrecedent += ","
                    rulePrecedent += ",".join(antecedent_pass)
                    each_temp = list(map(lambda s: s.strip(), rulePrecedent.split(',')))
                    if (rulePrecedent != ""):
                        startNew = False
                        for temp in each_temp:
                            if rulePrecedent not in mapDependenciesPercentTrue:
                                mapDependenciesPercentTrue[temp] = total
                                mapDependenciesPercentFalse[temp] = incorrect
                            else:
                                mapDependenciesPercentTrue[temp] += mapDependenciesPercentTrue[rulePrecedent] + total
                                mapDependenciesPercentFalse[temp] += mapDependenciesPercentFalse[
                                                                                 rulePrecedent] + incorrect
                else:
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    if percentage_total > 0:
                        percent_final = (percentage_total - percentage_incorrect) / percentage_total * 100
                    else:
                        percent_final = 0

                    if startNew is False and len(mapDependenciesPercentTrue)>0 and percent >= 90 and percent_final >= 90:
                        for key, value in mapDependenciesPercentTrue.items():
                            per_temp = (value - mapDependenciesPercentFalse[key])/value* 100
                            if per_temp >= 90:
                                mapDependencies.add(key)

                        for rulePrecedent in mapDependencies :
                            #print(rulePrecedent + "=" + consequent+"f")
                            if consequent in consequent_set and consequent not in tc_ignore_set:
                                each = list(map(lambda s: s.strip(), rulePrecedent.split(',')))
                                for temp in each:
                                    cons = match_name(temp)
                                    #print (cons + "=" + consequent+"p")
                                    names = set()
                                    if cons in tc_dependency:
                                        names = tc_dependency[cons]
                                    names.add(consequent+'p')
                                    tc_dependency[cons] = names
        for key, value in tc_dependency.items():
            print(key + "=", end="")
            rulePrecedent = ",".join(value)

            print(rulePrecedent)


def find_all_rules_pass_using_consequent2(consequent_set):
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        startNew = True
        mapDependencies = set()
        for line in f:
            if ('rules' in line):
                res = []
                startNew = True
                mapDependencies = set()
            if ("=>" in line):

                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if "failAll" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    if percent >= 90:
                        if len(antecedent_fail) >0:
                            res = change_list_result(antecedent_fail)
                        rulePrecedent = ",".join(res)
                        if (len(antecedent_fail) >0 and len(antecedent_pass) >0):
                            rulePrecedent += ","

                        rulePrecedent += ",".join(antecedent_pass)
                        if (len(rulePrecedent)>0):
                            print(rulePrecedent + "=" + consequent+"p")
                    if (rulePrecedent != "" and percent > 90):
                        mapDependencies.add(rulePrecedent)
                        startNew = False

                else:
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    if startNew is False and len(mapDependencies)>0:
                        if percent >= 90:
                            for rulePrecedent in mapDependencies :
                                #print(rulePrecedent + "=" + consequent+"f")
                                if consequent in consequent_set:
                                    each = list(map(lambda s: s.strip(), rulePrecedent.split(',')))
                                    for temp in each:
                                        cons = match_name(temp)
                                        #print (cons + "=" + consequent+"p")
                                        names = set()
                                        if cons in tc_dependency:
                                            names = tc_dependency[cons]

                                        names.add(consequent+'p')
                                        tc_dependency[cons] = names


def change_list_result(list):
    new_list_opposite_result =[]
    for text in list:
        cons = match_name(text)
        new_list_opposite_result.append(cons)
    return new_list_opposite_result


def find_all_rules_fail_using_consequent(consequent_set):
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        startNew = True
        mapDependencies = set()
        for line in f:
            if ('rules' in line):
                startNew = True
                mapDependencies = set()
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if "null" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    rulePrecedent = ",".join(antecedent_fail)
                    if (len(antecedent_fail) > 0 and len(antecedent_pass) > 0):
                        rulePrecedent += ","
                    rulePrecedent += ",".join(antecedent_pass)
                    if (rulePrecedent != ""):
                        mapDependencies.add(rulePrecedent)
                        startNew = False
                else:
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    if startNew is False and len(mapDependencies) > 0:
                        if percent >= 90:
                            for rulePrecedent in mapDependencies:
                                # print(rulePrecedent + "=" + consequent+"f")
                                if consequent in consequent_set:
                                    each = list(map(lambda s: s.strip(), rulePrecedent.split(',')))
                                    for temp in each:
                                        cons = match_name(temp)
                                        # print (cons + "=" + consequent+"p")
                                        names = set()
                                        if cons in tc_dependency:
                                            names = tc_dependency[cons]
                                        names.add(consequent + 'f')
                                        tc_dependency[cons] = names

        for key, value in tc_dependency.items():
            print(key + "=", end="")
            rulePrecedent = ",".join(value)

            print(rulePrecedent)


# replace result with opposite
def match_name(item):
    match = re.match(r"([0-9]+)([a-z]+)", item, re.I)
    if match:
        items = match.groups()
        if "p" in items[1]:
            return items[0]+'f'
        else:
            return items[0] + 'p'


def find_all_rules_pass():
    consequent_set = set()
    with open(file_rules) as f:
        rulePrecedent = None
        for line in f:
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if "null" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split(
                                               "/"))
                    percent = (total - incorrect) / total * 100

                    rulePrecedent = ",".join(antecedent_fail)
                    if (len(antecedent_fail) > 0 and len(antecedent_pass) > 0):
                        rulePrecedent += ","
                    rulePrecedent += ",".join(antecedent_pass)

                    if percent >= 90 and rulePrecedent !="" and total>4:
                        a=3
                        print(rulePrecedent + "=" + consequent + "p")
                    if percent >= 90 and rulePrecedent == "":
                        #print(consequent)
                        consequent_set.add(consequent)
    return consequent_set


def find_rules_pass_onetoone():
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        antecedent_tc = ""
        antecedent_result = ""
        for line in f:
            if "rules:" in line:
                different = True
            if ("=>" in line and "and" not in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if ("=" in antecedent_raw):
                    antecedent = antecedent_raw.split("=")
                    antecedent_tc = (antecedent[0].replace("(", "")).strip()
                    antecedent_result = (antecedent[1].replace(")", "")).strip()
                    different = False
                elif different is False:
                    result = ""
                    if antecedent_result=='failAll':
                        result = 'p'
                    else:
                        result = 'f'
                    if 'failAll' in consequent_raw:
                        result2 = 'f'
                    else:
                        result2 = 'p'

                    tc_name = antecedent_tc + result
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0] + result2
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split(
                                               "/"))
                    percent = (total - incorrect) / total * 100
                    if percent >= 90:
                        names = set()
                        if tc_name in tc_dependency:
                            names = tc_dependency[tc_name]
                        names.add(consequent)
                       # print (names)
                        tc_dependency[tc_name] = names

        for key, value in tc_dependency.items():
            val = set()
            val = val.union(value)
            for entryWithin in val.copy():
                oldKeys = set()
                getKeyWithin(val, entryWithin, oldKeys, tc_dependency, key);
            tc_dependency[key] = val

        for key, value in tc_dependency.items():
            print (key + "=",end ="")
            rulePrecedent = ",".join(value)
            print(rulePrecedent)


def getKeyWithin(tempSet, key, oldKeys, ruleNameMap, original_key):
    if (key != original_key):
        oldKeys.add(key)
    if key in ruleNameMap:
        for entryWithin in ruleNameMap[key]:
            if (entryWithin not in tempSet and entryWithin not in oldKeys):
                getKeyWithin(tempSet, entryWithin, oldKeys, ruleNameMap, original_key)
            else:
                if (entryWithin is not original_key):
                    tempSet.add(entryWithin)
        if (key != original_key):
            tempSet.add(key)
    if (key != original_key):
        tempSet.add(key)


def find_no_rules():
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        antecedent_tc = ""
        antecedent_result = ""
        for line in f:
            if "rules:" in line:
                different = True
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if different and antecedent_raw=='' and 'null' in line:
                    consequent, percentages_raw = consequent_raw.split("(")
                    tc, status = list(map(lambda s: s.strip(), consequent.split('=')))
                    total, incorrect = map(float, percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100
                    if percent == 100:
                        print(tc)
                different = False


def find_no_rules_fail():
    tc_dependency = {}
    with open(file_rules) as f:
        rulePrecedent = None
        antecedent_tc = ""
        antecedent_result = ""
        for line in f:
            if "rules:" in line:
                different = True
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))
                if different and antecedent_raw == '' and 'failAll' in line:
                    consequent, percentages_raw = consequent_raw.split("(")
                    tc, status = list(map(lambda s: s.strip(), consequent.split('=')))
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split(
                                               "/"))
                    percent = (total - incorrect) / total * 100
                    if percent == 100 and total>10:
                        print(tc)
                different = False


#occurrences in all
def find_all_rules_fail_single():
    consequent_set = set()
    with open(file_rules) as f:
        rulePrecedent = None
        for line in f:
            if ('rules' in line):
                startNew = True
                mapDependencies = set()
                tc_ignore_set = set()
                mapDependenciesPercentTrue = {}
                mapDependenciesPercentFalse = {}
            if ("=>" in line):
                line = line.strip()
                antecedent_raw, consequent_raw = list(map(lambda s: s.strip(), line.split('=>')))

                if '13957 = failAll) and (13856 = failAll) and (13807 = failAll' in line:
                    a=3
                if "failAll" in consequent_raw:
                    antecedent_fail = extract_ids_all_failed(antecedent_raw.split("and"))
                    antecedent_pass = extract_ids_all_passed(antecedent_raw.split("and"))
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    rulePrecedent = ",".join(antecedent_fail)
                    if (len(antecedent_fail) >0 and len(antecedent_pass) >0):
                        rulePrecedent += ","
                    rulePrecedent += ",".join(antecedent_pass)
                    each_temp = list(map(lambda s: s.strip(), rulePrecedent.split(',')))

                    if rulePrecedent !="" and total>4:
                        if startNew:
                            for temp in each_temp:
                                mapDependenciesPercentTrue[temp] = total
                                mapDependenciesPercentFalse[temp] = incorrect
                                mapDependencies.add(temp)

                        else:
                            newDependencies = set()
                            for temp in each_temp:
                                newDependencies.add(temp)
                                if temp in mapDependenciesPercentTrue:
                                    mapDependenciesPercentTrue[temp] += total
                                    mapDependenciesPercentFalse[temp] += incorrect
                            mapDependencies = mapDependencies.intersection(newDependencies)
                    else:
                        mapDependencies.clear()
                    startNew = False
                else:
                    consequent, percentages_raw = consequent_raw.split("(")
                    consequent = extract_All([consequent])[0]
                    total, incorrect = map(float,
                                           percentages_raw.strip().replace("(", "").replace(")", "").split("/"))
                    percent = (total - incorrect) / total * 100

                    if startNew is False and len(mapDependencies)>0 and percent >90:
                        for anteced in mapDependencies:
                            percent_new = (mapDependenciesPercentTrue[anteced]-mapDependenciesPercentFalse[anteced])/mapDependenciesPercentTrue[anteced]*100
                            if percent_new >90:
                                print(anteced + "=" + consequent + "f")
    return consequent_set


def findPassBasedOnFailed():
    consequent_set = find_all_rules_pass()
    find_all_rules_pass_using_consequent_allHigher(consequent_set)


def findCommaPassBasedOnFailed():
    consequent_set = find_all_rules_pass()
    find_all_rules_pass_using_consequent2(consequent_set)


def findFailBasedOnPassed():
    consequent_set = find_all_rules_fail()
    find_all_rules_fail_using_consequent(consequent_set)


find_all_rules_fail()
find_all_rules_pass()
findPassBasedOnFailed()
findCommaPassBasedOnFailed()
findFailBasedOnPassed()
find_all_rules_fail_single()

#find no rules always passes
find_no_rules()

#find no rules always fails
find_no_rules_fail()