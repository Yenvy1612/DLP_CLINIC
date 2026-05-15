import { geminiAsk } from "./http";

export const geminiService = {
    async ask(prompt) {
        return geminiAsk(prompt);
    },
};
