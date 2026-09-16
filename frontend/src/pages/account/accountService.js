import api from "../../services/api";

const accountService = {

    // GET /api/users
    getAccounts: async () => {

        const response =
            await api.get("/users");

        return response.data;
    },


    // GET /api/users/{id}
    getAccountById: async (id) => {

        const response =
            await api.get(`/users/${id}`);

        return response.data;
    },


    // POST /api/users
    createAccount: async (account) => {

        const response =
            await api.post("/users", account);

        return response.data;
    },


    // PUT /api/users/{id}
    updateAccount: async (id, account) => {

        const response =
            await api.put(
                `/users/${id}`,
                account
            );

        return response.data;
    },


    // DELETE /api/users/{id}
    deleteAccount: async (id) => {

        await api.delete(
            `/users/${id}`
        );
    }

};

export default accountService;