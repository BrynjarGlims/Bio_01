import matplotlib.pyplot as plt
# indicate the output of plotting function is printed to the notebook



def main():
    fname = "data/histories/PLOT"

    with open(fname) as f:
        content = f.readlines()
    # you may also want to remove whitespace characters like `\n` at the end of each line
    string_content = [x.strip().split() for x in content]

    content = []
    for line in string_content:
        content.append([float(i) for i in line])



    for index, line in enumerate(content):
        plt.plot(line, label=index)


    plt.legend()
    plt.show()


if __name__ == "__main__":
    main()
