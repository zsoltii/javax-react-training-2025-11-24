document.addEventListener("DOMContentLoaded", () => {
  window.fullResponseContent = "";

  document.getElementById("chatButton").addEventListener("click", async () => {
    window.stopSpeech();
    const questionInput = document.getElementById("questionInput");
    const chatButton = document.getElementById("chatButton");
    const question = questionInput.value;

    if (!question) return;

    const thinkingDiv = document.getElementById("thinking");
    const responseDiv = document.getElementById("response");
    const thinkingTimerDisplay = document.getElementById(
      "thinkingTimerDisplay",
    );
    const thinkingStatsDisplay = document.getElementById(
      "thinkingStatsDisplay",
    );
    const responseTimerDisplay = document.getElementById(
      "responseTimerDisplay",
    );
    const responseStatsDisplay = document.getElementById(
      "responseStatsDisplay",
    );

    thinkingDiv.innerHTML = "";
    responseDiv.innerHTML = "";
    thinkingTimerDisplay.textContent = "";
    thinkingStatsDisplay.textContent = "";
    responseTimerDisplay.textContent = "";
    responseStatsDisplay.textContent = "";
    let fullThinkingContent = "";
    window.fullResponseContent = "";
    let thinkingTimerInterval = null;
    let responseTimerInterval = null;

    chatButton.disabled = true;
    questionInput.disabled = true;
    const originalButtonLabel = chatButton.querySelector(".mdc-button__label");
    const originalButtonContent = originalButtonLabel.innerHTML;
    originalButtonLabel.innerHTML = '<div class="loader"></div>';

    const formatTime = (totalSeconds, precision = 0) => {
      const minutes = Math.floor(totalSeconds / 60);
      const seconds = (totalSeconds % 60).toFixed(precision);
      return `${minutes} perc ${seconds} másodperc`;
    };

    const updateStats = (content, displayElement) => {
      const charCount = content.length;
      const trimmedContent = content.trim();
      const wordCount =
        trimmedContent === "" ? 0 : trimmedContent.split(/\s+/).length;
      displayElement.textContent = `Szavak: ${wordCount} | Karakterek: ${charCount}`;
    };

    const thinkingStartTime = Date.now();
    thinkingTimerInterval = setInterval(() => {
      const elapsedTime = (Date.now() - thinkingStartTime) / 1000;
      thinkingTimerDisplay.textContent = `Generálási idő: ${formatTime(elapsedTime, 1)}`;
    }, 100);

    let responseStartTime = null;

    const scrollToBottom = () => window.scrollTo(0, document.body.scrollHeight);
    const isUserAtBottom = () =>
      window.innerHeight + window.scrollY >= document.body.scrollHeight - 20;

    try {
      const response = await fetch("/api/ollama/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ question: question }),
      });

      if (!response.ok)
        throw new Error(`HTTP hiba! Státusz: ${response.status}`);

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = "";

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split("\n");
        buffer = lines.pop();

        for (const line of lines) {
          if (line.startsWith("data:")) {
            const jsonStr = line.substring(5).trim();
            if (jsonStr) {
              try {
                const data = JSON.parse(jsonStr);
                const shouldScroll = isUserAtBottom();

                if (data.thinking) {
                  fullThinkingContent += data.thinking;
                  thinkingDiv.innerHTML = marked.parse(fullThinkingContent);
                  updateStats(fullThinkingContent, thinkingStatsDisplay);
                }

                if (data.message) {
                  if (!responseStartTime) {
                    responseStartTime = Date.now();
                    clearInterval(thinkingTimerInterval);
                    const finalThinkingTime =
                      (responseStartTime - thinkingStartTime) / 1000;
                    thinkingTimerDisplay.textContent = `Generálási idő: ${formatTime(finalThinkingTime, 2)}`;
                    updateStats(fullThinkingContent, thinkingStatsDisplay);

                    responseTimerInterval = setInterval(() => {
                      const elapsedTime =
                        (Date.now() - responseStartTime) / 1000;
                      responseTimerDisplay.textContent = `Generálási idő: ${formatTime(elapsedTime, 1)}`;
                    }, 100);
                  }
                  window.fullResponseContent += data.message;
                  responseDiv.innerHTML = marked.parse(
                    window.fullResponseContent,
                  );
                  updateStats(window.fullResponseContent, responseStatsDisplay);
                }

                if (shouldScroll) scrollToBottom();
              } catch (e) {
                console.error("Hiba a JSON parse-oláskor:", e, "Line:", line);
              }
            }
          }
        }
      }
    } catch (error) {
      console.error("Hiba történt:", error);
      responseDiv.innerHTML += `<p style="color: red;">\n\nHiba történt a kommunikáció során: ${error.message}</p>`;
      scrollToBottom();
    } finally {
      clearInterval(thinkingTimerInterval);
      clearInterval(responseTimerInterval);

      if (responseStartTime) {
        const finalResponseTime = (Date.now() - responseStartTime) / 1000;
        responseTimerDisplay.textContent = `Generálási idő: ${formatTime(finalResponseTime, 2)}`;
      } else {
        const finalThinkingTime = (Date.now() - thinkingStartTime) / 1000;
        thinkingTimerDisplay.textContent = `Generálási idő: ${formatTime(finalThinkingTime, 2)}`;
      }

      updateStats(fullThinkingContent, thinkingStatsDisplay);
      updateStats(window.fullResponseContent, responseStatsDisplay);

      chatButton.disabled = false;
      questionInput.disabled = false;
      originalButtonLabel.innerHTML = originalButtonContent;
      questionInput.focus();
    }
  });

  document
    .getElementById("questionInput")
    .addEventListener("keydown", function (e) {
      if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        document.getElementById("chatButton").click();
      }
    });

  document.getElementById("clearQuestion").addEventListener("click", () => {
    const questionInput = document.getElementById("questionInput");
    questionInput.value = "";
    const textField = textFields.find((tf) => tf.root.contains(questionInput));
    if (textField) {
      textField.value = "";
      textField.layout();
    }
    questionInput.focus();
  });
});
