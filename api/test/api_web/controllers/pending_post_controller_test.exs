defmodule ApiWeb.PendingPostControllerTest do
  use ApiWeb.ConnCase

  alias Api.Content
  alias Api.Content.PendingPost

  @create_attrs %{caption: "some caption", image: "some image", title: "some title"}
  @update_attrs %{caption: "some updated caption", image: "some updated image", title: "some updated title"}
  @invalid_attrs %{caption: nil, image: nil, title: nil}

  def fixture(:pending_post) do
    {:ok, pending_post} = Content.create_pending_post(@create_attrs)
    pending_post
  end

  setup %{conn: conn} do
    {:ok, conn: put_req_header(conn, "accept", "application/json")}
  end

  describe "index" do
    test "lists all pending", %{conn: conn} do
      conn = get conn, pending_post_path(conn, :index)
      assert json_response(conn, 200)["data"] == []
    end
  end

  describe "create pending_post" do
    test "renders pending_post when data is valid", %{conn: conn} do
      conn = post conn, pending_post_path(conn, :create), pending_post: @create_attrs
      assert %{"id" => id} = json_response(conn, 201)["data"]

      conn = get conn, pending_post_path(conn, :show, id)
      assert json_response(conn, 200)["data"] == %{
        "id" => id,
        "caption" => "some caption",
        "image" => "some image",
        "title" => "some title"}
    end

    test "renders errors when data is invalid", %{conn: conn} do
      conn = post conn, pending_post_path(conn, :create), pending_post: @invalid_attrs
      assert json_response(conn, 422)["errors"] != %{}
    end
  end

  describe "update pending_post" do
    setup [:create_pending_post]

    test "renders pending_post when data is valid", %{conn: conn, pending_post: %PendingPost{id: id} = pending_post} do
      conn = put conn, pending_post_path(conn, :update, pending_post), pending_post: @update_attrs
      assert %{"id" => ^id} = json_response(conn, 200)["data"]

      conn = get conn, pending_post_path(conn, :show, id)
      assert json_response(conn, 200)["data"] == %{
        "id" => id,
        "caption" => "some updated caption",
        "image" => "some updated image",
        "title" => "some updated title"}
    end

    test "renders errors when data is invalid", %{conn: conn, pending_post: pending_post} do
      conn = put conn, pending_post_path(conn, :update, pending_post), pending_post: @invalid_attrs
      assert json_response(conn, 422)["errors"] != %{}
    end
  end

  describe "delete pending_post" do
    setup [:create_pending_post]

    test "deletes chosen pending_post", %{conn: conn, pending_post: pending_post} do
      conn = delete conn, pending_post_path(conn, :delete, pending_post)
      assert response(conn, 204)
      assert_error_sent 404, fn ->
        get conn, pending_post_path(conn, :show, pending_post)
      end
    end
  end

  defp create_pending_post(_) do
    pending_post = fixture(:pending_post)
    {:ok, pending_post: pending_post}
  end
end
